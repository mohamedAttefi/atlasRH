package ma.atlasrh.service;

import ma.atlasrh.dto.*;
import ma.atlasrh.entity.*;
import ma.atlasrh.repository.*;
import ma.atlasrh.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ContractService {
    private final ContractRepository repo;
    private final EmployeeService employees;

    public ContractService(ContractRepository r, EmployeeService e) {
        repo = r;
        employees = e;
    }

    public ContractResponse create(ContractRequest r) {
        if ((r.type() == ContractType.CDD || r.type() == ContractType.STAGE) && r.endDate() == null)
            throw new IllegalArgumentException("La date de fin est obligatoire pour un CDD ou un STAGE");
        if (r.endDate() != null && r.endDate().isBefore(r.startDate()))
            throw new IllegalArgumentException("La date de fin précède la date de début");
        if (repo.existsByEmployeeIdAndActiveTrue(r.employeeId()))
            throw new ConflictException("L'employé possède déjà un contrat actif");
        Contract c = new Contract();
        c.setEmployee(employees.get(r.employeeId()));
        c.setType(r.type());
        c.setStartDate(r.startDate());
        c.setEndDate(r.endDate());
        c.setGrossSalary(r.grossSalary());
        return response(repo.save(c));
    }

    public ContractResponse end(Long id) {
        Contract c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable"));
        if (!c.isActive()) throw new ConflictException("Contrat déjà terminé");
        c.setActive(false);
        if (c.getEndDate() == null) c.setEndDate(java.time.LocalDate.now());
        return response(c);
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> history(Long employee) {
        employees.get(employee);
        return repo.findByEmployeeIdOrderByStartDateDesc(employee).stream().map(this::response).toList();
    }

    private ContractResponse response(Contract c) {
        return new ContractResponse(c.getId(), c.getEmployee().getId(), c.getType(), c.getStartDate(), c.getEndDate(), c.getGrossSalary(), c.isActive());
    }
}
