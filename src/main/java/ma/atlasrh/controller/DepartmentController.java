
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.DepartmentRequest;
import ma.atlasrh.dto.DepartmentResponse;
import ma.atlasrh.entity.Department;
import ma.atlasrh.mapper.DepartmentMapper;
import ma.atlasrh.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    public DepartmentController(
            DepartmentService departmentService,
            DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(
            @Valid @RequestBody DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);
        Department saved = departmentService.createDepartment(department);

        return departmentMapper.toResponse(saved);
    }

    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentService.getAllDepartments()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable Long id) {
        return departmentMapper.toResponse(
                departmentService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);
        Department updated =
                departmentService.updateDepartment(id, department);

        return departmentMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}
