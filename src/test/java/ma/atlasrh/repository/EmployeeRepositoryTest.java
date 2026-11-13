package ma.atlasrh.repository;

import ma.atlasrh.entity.Department;
import ma.atlasrh.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {
    @Autowired EmployeeRepository employees;
    @Autowired DepartmentRepository departments;

    @Test
    void shouldFilterEmployeesAndApplyPageAndSort() {
        Department department = departments.save(new Department("Engineering", "Product team"));
        employees.save(employee("AT-1", "Zara", "Benali", department));
        employees.save(employee("AT-2", "Amine", "Alaoui", department));
        employees.save(employee("AT-3", "Meryem", "Other", department));

        Page<Employee> firstPage = employees.findByDepartmentId(department.getId(),
                PageRequest.of(0, 1, Sort.by("lastName").ascending()));
        Page<Employee> search = employees.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
                "Zara", "Zara", PageRequest.of(0, 10));

        assertEquals(3, firstPage.getTotalElements());
        assertEquals(1, firstPage.getContent().size());
        assertEquals("Alaoui", firstPage.getContent().get(0).getLastName());
        assertEquals(1, search.getTotalElements());
        assertEquals("Benali", search.getContent().get(0).getLastName());
    }

    private Employee employee(String matricule, String firstName, String lastName, Department department) {
        Employee employee = new Employee();
        employee.setMatricule(matricule);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(matricule.toLowerCase() + "@example.test");
        employee.setPosition("Developer");
        employee.setHireDate(LocalDate.of(2024, 1, 1));
        employee.setDepartment(department);
        return employee;
    }
}
