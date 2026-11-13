package ma.atlasrh.service;

import ma.atlasrh.dto.EmployeeRequest;
import ma.atlasrh.entity.Department;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.exception.ConflictException;
import ma.atlasrh.repository.DepartmentRepository;
import ma.atlasrh.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock EmployeeRepository employees;
    @Mock DepartmentRepository departments;
    @InjectMocks EmployeeService service;

    @Test
    void shouldCreateEmployeeWhenMatriculeAndEmailAreUnique() {
        EmployeeRequest request = request("AT-10", "a@example.test", null);
        Department department = new Department("RH", "Ressources humaines");
        when(departments.findById(1L)).thenReturn(Optional.of(department));
        when(employees.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertEquals("AT-10", response.matricule());
        assertEquals("a@example.test", response.email());
        verify(employees).save(any(Employee.class));
    }

    @Test
    void shouldRejectDuplicateMatricule() {
        when(employees.existsByMatriculeIgnoreCase("AT-10")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request("AT-10", "a@example.test", null)));
        verify(employees, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        when(employees.existsByEmailIgnoreCase("a@example.test")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request("AT-10", "a@example.test", null)));
        verify(employees, never()).save(any());
    }

    @Test
    void shouldRejectEmployeeAsOwnManager() {
        Employee existing = new Employee();
        setId(existing, 7L);
        when(employees.findById(7L)).thenReturn(Optional.of(existing));
        when(employees.findAll()).thenReturn(java.util.List.of(existing));
        when(departments.findById(1L)).thenReturn(Optional.of(new Department("RH", "")));

        assertThrows(IllegalArgumentException.class, () -> service.update(7L, request("AT-10", "a@example.test", 7L)));
    }

    private EmployeeRequest request(String matricule, String email, Long managerId) {
        return new EmployeeRequest(matricule, "Amina", "Test", email, "Analyste",
                LocalDate.of(2024, 1, 2), 1L, managerId);
    }

    private void setId(Employee employee, Long id) {
        try {
            var field = Employee.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(employee, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
