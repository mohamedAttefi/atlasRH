
package ma.atlasrh.service;

import ma.atlasrh.entity.Department;
import ma.atlasrh.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByNameIgnoreCase(
                department.getName())) {
            throw new IllegalArgumentException(
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "Department not found: " + id));
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existing = getDepartmentById(id);

        boolean nameChanged = !existing.getName().equalsIgnoreCase(
                updatedDepartment.getName());

        if (nameChanged && departmentRepository.existsByNameIgnoreCase(
                updatedDepartment.getName())) {
            throw new IllegalArgumentException(
                    "A department with this name already exists");
        }

        existing.setName(updatedDepartment.getName());
        existing.setDescription(updatedDepartment.getDescription());

        return departmentRepository.save(existing);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);

        departmentRepository.delete(department);
    }
}
