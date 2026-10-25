package ma.atlasrh.repository;

import ma.atlasrh.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByDepartmentId(Long id);

    boolean existsByMatriculeIgnoreCase(String v);

    boolean existsByEmailIgnoreCase(String v);

    Page<Employee> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String a, String b, Pageable p);

    Page<Employee> findByDepartmentId(Long id, Pageable p);

    java.util.List<Employee> findByManagerId(Long id);
}
