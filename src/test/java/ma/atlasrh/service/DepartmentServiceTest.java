package ma.atlasrh.service;

import ma.atlasrh.entity.Department;
import ma.atlasrh.exception.ConflictException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.DepartmentRepository;
import ma.atlasrh.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock DepartmentRepository departments;
    @Mock EmployeeRepository employees;
    @InjectMocks DepartmentService service;

    @Test
    void shouldCreateDepartmentWithUniqueName() {
        Department department = new Department("Finance", "Finance team");
        when(departments.save(department)).thenReturn(department);

        assertSame(department, service.createDepartment(department));
        verify(departments).existsByNameIgnoreCase("Finance");
    }

    @Test
    void shouldRejectDuplicateDepartmentName() {
        when(departments.existsByNameIgnoreCase("Finance")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.createDepartment(new Department("Finance", "")));
        verify(departments, never()).save(any());
    }

    @Test
    void shouldUpdateExistingDepartmentAndAllowUnchangedName() {
        Department existing = new Department("Finance", "Old");
        when(departments.findById(4L)).thenReturn(Optional.of(existing));
        when(departments.save(existing)).thenReturn(existing);

        Department result = service.updateDepartment(4L, new Department("FINANCE", "Updated"));

        assertSame(existing, result);
        assertEquals("Updated", result.getDescription());
        verify(departments, never()).existsByNameIgnoreCase(anyString());
    }

    @Test
    void shouldRejectRenameToAnotherDepartmentsName() {
        when(departments.findById(4L)).thenReturn(Optional.of(new Department("Finance", "")));
        when(departments.existsByNameIgnoreCase("RH")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.updateDepartment(4L, new Department("RH", "")));
    }

    @Test
    void shouldFindDepartmentOrReportNotFound() {
        Department department = new Department("Finance", "");
        when(departments.findById(4L)).thenReturn(Optional.of(department));
        when(departments.findAll()).thenReturn(List.of(department));

        assertSame(department, service.getDepartmentById(4L));
        assertEquals(List.of(department), service.getAllDepartments());
        when(departments.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getDepartmentById(9L));
    }

    @Test
    void shouldDeleteEmptyDepartment() {
        Department department = new Department("Finance", "");
        when(departments.findById(4L)).thenReturn(Optional.of(department));
        when(employees.existsByDepartmentId(4L)).thenReturn(false);

        service.deleteDepartment(4L);

        verify(departments).delete(department);
    }

    @Test
    void shouldRefuseDeletingOccupiedDepartment() {
        when(departments.findById(4L)).thenReturn(Optional.of(new Department("Finance", "")));
        when(employees.existsByDepartmentId(4L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.deleteDepartment(4L));
        verify(departments, never()).delete(any());
    }
}
