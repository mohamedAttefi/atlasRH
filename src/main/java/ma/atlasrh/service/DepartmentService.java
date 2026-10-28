
package ma.atlasrh.service;

import ma.atlasrh.entity.Department;
import ma.atlasrh.repository.DepartmentRepository;
import ma.atlasrh.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByNameIgnoreCase(
                department.getName())) {
            throw new ma.atlasrh.exception.ConflictException(
                    "A department with this name already exists");
        }

        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ma.atlasrh.exception.ResourceNotFoundException(
                        "Department not found: " + id));
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existing = getDepartmentById(id);

        boolean nameChanged = !existing.getName().equalsIgnoreCase(
                updatedDepartment.getName());

        if (nameChanged && departmentRepository.existsByNameIgnoreCase(
                updatedDepartment.getName())) {
            throw new ma.atlasrh.exception.ConflictException(
                    "A department with this name already exists");
        }

        existing.setName(updatedDepartment.getName());
        existing.setDescription(updatedDepartment.getDescription());

        return departmentRepository.save(existing);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new ma.atlasrh.exception.ConflictException("Department contains employees");
        }
        departmentRepository.delete(department);
    }
}
