package ma.atlasrh.service;

import ma.atlasrh.dto.ContractRequest;
import ma.atlasrh.entity.Contract;
import ma.atlasrh.entity.ContractType;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.exception.ConflictException;
import ma.atlasrh.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {
    @Mock ContractRepository contracts;
    @Mock EmployeeService employees;
    @InjectMocks ContractService service;

    @Test
    void shouldCreateContractWhenEmployeeHasNoActiveContract() {
        Employee employee = new Employee();
        when(contracts.existsByEmployeeIdAndActiveTrue(4L)).thenReturn(false);
        when(employees.get(4L)).thenReturn(employee);
        when(contracts.save(any(Contract.class))).thenAnswer(call -> call.getArgument(0));

        var response = service.create(new ContractRequest(4L, ContractType.CDI,
                LocalDate.of(2025, 1, 1), null, new BigDecimal("12000")));

        assertTrue(response.active());
        assertEquals(ContractType.CDI, response.type());
    }

    @Test
    void shouldRejectSecondActiveContract() {
        when(contracts.existsByEmployeeIdAndActiveTrue(4L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(new ContractRequest(4L,
                ContractType.CDI, LocalDate.of(2025, 1, 1), null, new BigDecimal("12000"))));
        verify(contracts, never()).save(any());
    }

    @Test
    void shouldEndActiveContractAndSetEndDateWhenMissing() {
        Employee employee = new Employee();
        setId(employee, 4L);
        Contract contract = new Contract();
        contract.setEmployee(employee);
        contract.setType(ContractType.CDI);
        contract.setStartDate(LocalDate.of(2025, 1, 1));
        contract.setGrossSalary(new BigDecimal("12000"));
        when(contracts.findById(8L)).thenReturn(Optional.of(contract));

        var response = service.end(8L);

        assertFalse(response.active());
        assertEquals(LocalDate.now(), response.endDate());
    }

    @Test
    void shouldRejectEndingAnAlreadyEndedContract() {
        Contract contract = new Contract();
        contract.setActive(false);
        when(contracts.findById(8L)).thenReturn(Optional.of(contract));

        assertThrows(ConflictException.class, () -> service.end(8L));
    }

    @Test
    void shouldListEmployeeContractHistory() {
        Employee employee = new Employee();
        setId(employee, 4L);
        when(employees.get(4L)).thenReturn(employee);
        when(contracts.findByEmployeeIdOrderByStartDateDesc(4L)).thenReturn(List.of());

        assertTrue(service.history(4L).isEmpty());
    }

    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
