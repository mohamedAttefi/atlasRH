package ma.atlasrh.service;

import ma.atlasrh.dto.*;
import ma.atlasrh.entity.*;
import ma.atlasrh.repository.*;
import ma.atlasrh.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class LeaveService {
    private final LeaveRequestRepository requests;
    private final LeaveTypeRepository types;
    private final EmployeeService employees;

    public LeaveService(LeaveRequestRepository r, LeaveTypeRepository t, EmployeeService e) {
        requests = r;
        types = t;
        employees = e;
    }

    public LeaveResponse create(LeaveRequestCreate r) {
        if (r.endDate().isBefore(r.startDate()))
            throw new IllegalArgumentException("La date de fin précède la date de début");
        if (requests.existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(r.employeeId(), List.of(LeaveStatus.EN_ATTENTE, LeaveStatus.APPROUVEE), r.endDate(), r.startDate()))
            throw new ConflictException("Une demande chevauche déjà cette période");
        Employee emp = employees.get(r.employeeId());
        LeaveType type = types.findById(r.leaveTypeId()).orElseThrow(() -> new ResourceNotFoundException("Type de congé introuvable"));
        int days = workingDays(r.startDate(), r.endDate());
        LeaveRequest x = new LeaveRequest();
        x.setEmployee(emp);
        x.setLeaveType(type);
        x.setStartDate(r.startDate());
        x.setEndDate(r.endDate());
        x.setReason(r.reason());
        x.setRequestedDays(days);
        return response(requests.save(x));
    }

    public List<LeaveResponse> mine(Long employee, LeaveStatus status) {
        employees.get(employee);
        return requests.findByEmployeeId(employee).stream().filter(r -> status == null || r.getStatus() == status).map(this::response).toList();
    }

    public LeaveResponse cancel(Long employee, Long id) {
        LeaveRequest r = owned(employee, id);
        if (r.getStatus() != LeaveStatus.EN_ATTENTE)
            throw new ConflictException("Seule une demande en attente peut être annulée");
        r.setStatus(LeaveStatus.ANNULEE);
        return response(r);
    }

    public List<LeaveResponse> pendingTeam(Long manager) {
        employees.get(manager);
        return requests.findByEmployeeManagerIdAndStatus(manager, LeaveStatus.EN_ATTENTE).stream().map(this::response).toList();
    }

    public LeaveResponse decide(Long manager, Long id, LeaveDecision d) {
        LeaveRequest r = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));
        if (r.getEmployee().getManager() == null || !r.getEmployee().getManager().getId().equals(manager))
            throw new ConflictException("Cette demande ne concerne pas votre équipe");
        if (r.getStatus() != LeaveStatus.EN_ATTENTE) throw new ConflictException("La demande n'est plus en attente");
        if (!d.approved() && (d.comment() == null || d.comment().isBlank()))
            throw new IllegalArgumentException("Un refus exige un commentaire");
        if (d.approved()) {
            int used = 0;
            for (LeaveRequest old : requests.findByEmployeeIdAndLeaveTypeIdAndStatusAndStartDateBetween(r.getEmployee().getId(), r.getLeaveType().getId(), LeaveStatus.APPROUVEE, LocalDate.of(r.getStartDate().getYear(), 1, 1), LocalDate.of(r.getStartDate().getYear(), 12, 31)))
                used += old.getRequestedDays();
            if (used + r.getRequestedDays() > r.getLeaveType().getDaysPerYear())
                throw new ConflictException("Solde de congés insuffisant");
            r.setStatus(LeaveStatus.APPROUVEE);
        } else r.setStatus(LeaveStatus.REFUSEE);
        r.setComment(d.comment());
        return response(r);
    }

    public List<Map<String, Object>> balances(Long employee) {
        Employee e = employees.get(employee);
        return types.findAll().stream().map(t -> {
            int used = requests.findByEmployeeIdAndLeaveTypeIdAndStatusAndStartDateBetween(employee, t.getId(), LeaveStatus.APPROUVEE, LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.of(LocalDate.now().getYear(), 12, 31)).stream().mapToInt(LeaveRequest::getRequestedDays).sum();
            return Map.<String, Object>of("type", t.getName(), "daysPerYear", t.getDaysPerYear(), "used", used, "remaining", Math.max(0, t.getDaysPerYear() - used));
        }).toList();
    }

    public Map<String, Object> seniority(Long id) {
        LocalDate hire = employees.get(id).getHireDate();
        Period p = Period.between(hire, LocalDate.now());
        return Map.of("years", p.getYears(), "months", p.getMonths());
    }

    private LeaveRequest owned(Long employee, Long id) {
        LeaveRequest r = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));
        if (!r.getEmployee().getId().equals(employee))
            throw new ConflictException("Cette demande ne vous appartient pas");
        return r;
    }

    private int workingDays(LocalDate start, LocalDate end) {
        int n = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1))
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) n++;
        return n;
    }

    private LeaveResponse response(LeaveRequest r) {
        return new LeaveResponse(r.getId(), r.getEmployee().getId(), r.getLeaveType().getName(), r.getStartDate(), r.getEndDate(), r.getRequestedDays(), r.getReason(), r.getStatus(), r.getComment());
    }
}
