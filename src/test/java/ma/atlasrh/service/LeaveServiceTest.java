package ma.atlasrh.service;

import ma.atlasrh.dto.LeaveDecision;
import ma.atlasrh.dto.LeaveRequestCreate;
import ma.atlasrh.entity.*;
import ma.atlasrh.exception.ConflictException;
import ma.atlasrh.repository.LeaveRequestRepository;
import ma.atlasrh.repository.LeaveTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {
    @Mock LeaveRequestRepository requests;
    @Mock LeaveTypeRepository types;
    @Mock EmployeeService employees;
    @InjectMocks LeaveService service;

    @Test
    void shouldCountWeekdaysForLeaveRequest() {
        Employee employee = new Employee();
        LeaveType type = new LeaveType();
        when(employees.get(2L)).thenReturn(employee);
        when(types.findById(1L)).thenReturn(Optional.of(type));
        when(requests.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));

        var response = service.create(new LeaveRequestCreate(2L, 1L,
                LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 13), "Repos"));

        assertEquals(5, response.requestedDays());
        assertEquals(LeaveStatus.EN_ATTENTE, response.status());
    }

    @Test
    void shouldRejectReversedLeaveDates() {
        var request = new LeaveRequestCreate(2L, 1L, LocalDate.of(2026, 11, 14),
                LocalDate.of(2026, 11, 13), "Repos");

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verifyNoInteractions(requests, types, employees);
    }

    @Test
    void shouldApprovePendingRequestWithinBalance() {
        Employee manager = new Employee();
        Employee employee = new Employee();
        setId(manager, 1L);
        setId(employee, 2L);
        employee.setManager(manager);
        LeaveType type = new LeaveType();
        setId(type, 3L);
        type.setName("ANNUEL");
        type.setDaysPerYear(22);
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(type);
        leave.setStartDate(LocalDate.of(2026, 12, 1));
        leave.setEndDate(LocalDate.of(2026, 12, 2));
        leave.setRequestedDays(2);
        when(requests.findById(9L)).thenReturn(Optional.of(leave));
        when(requests.findByEmployeeIdAndLeaveTypeIdAndStatusAndStartDateBetween(anyLong(), anyLong(),
                eq(LeaveStatus.APPROUVEE), any(), any())).thenReturn(List.of());

        var result = service.decide(1L, 9L, new LeaveDecision(true, null));

        assertEquals(LeaveStatus.APPROUVEE, result.status());
    }

    @Test
    void shouldRejectApprovalWhenBalanceIsInsufficient() {
        Employee manager = new Employee();
        Employee employee = new Employee();
        setId(manager, 1L);
        setId(employee, 2L);
        employee.setManager(manager);
        LeaveType type = new LeaveType();
        setId(type, 3L);
        type.setName("ANNUEL");
        type.setDaysPerYear(22);
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(type);
        leave.setStartDate(LocalDate.of(2026, 12, 1));
        leave.setRequestedDays(2);
        LeaveRequest used = new LeaveRequest();
        used.setRequestedDays(22);
        when(requests.findById(9L)).thenReturn(Optional.of(leave));
        when(requests.findByEmployeeIdAndLeaveTypeIdAndStatusAndStartDateBetween(anyLong(), anyLong(),
                eq(LeaveStatus.APPROUVEE), any(), any())).thenReturn(List.of(used));

        assertThrows(ConflictException.class, () -> service.decide(1L, 9L, new LeaveDecision(true, null)));
    }

    @Test
    void shouldCancelOwnedPendingRequest() {
        Employee employee = new Employee();
        setId(employee, 2L);
        LeaveType type = new LeaveType();
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(type);
        leave.setStartDate(LocalDate.of(2026, 11, 9));
        leave.setEndDate(LocalDate.of(2026, 11, 9));
        when(requests.findById(9L)).thenReturn(Optional.of(leave));

        var result = service.cancel(2L, 9L);

        assertEquals(LeaveStatus.ANNULEE, result.status());
    }

    @Test
    void shouldRejectCancellationAfterDecision() {
        Employee employee = new Employee();
        setId(employee, 2L);
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStatus(LeaveStatus.APPROUVEE);
        when(requests.findById(9L)).thenReturn(Optional.of(leave));

        assertThrows(ConflictException.class, () -> service.cancel(2L, 9L));
    }

    private static void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
