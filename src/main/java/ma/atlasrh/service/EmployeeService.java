package ma.atlasrh.service;

import ma.atlasrh.entity.*;
import ma.atlasrh.repository.*;
import ma.atlasrh.dto.*;
import ma.atlasrh.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.*;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;

    public EmployeeService(EmployeeRepository e, DepartmentRepository d) {
        employees = e;
        departments = d;
    }

    public EmployeeResponse create(EmployeeRequest r) {
        if (employees.existsByMatriculeIgnoreCase(r.matricule()) || employees.existsByEmailIgnoreCase(r.email()))
            throw new ConflictException("Matricule ou e-mail déjà utilisé");
        Employee e = new Employee();
        apply(e, r);
        return response(employees.save(e));
    }

    public EmployeeResponse update(Long id, EmployeeRequest r) {
        Employee e = get(id);
        if (employees.findAll().stream().anyMatch(x -> !x.getId().equals(id) && (x.getEmail().equalsIgnoreCase(r.email()) || x.getMatricule().equalsIgnoreCase(r.matricule()))))
            throw new ConflictException("Matricule ou e-mail déjà utilisé");
        apply(e, r);
        return response(e);
    }

    private void apply(Employee e, EmployeeRequest r) {
        e.setMatricule(r.matricule());
        e.setFirstName(r.firstName());
        e.setLastName(r.lastName());
        e.setEmail(r.email());
        e.setPosition(r.position());
        e.setHireDate(r.hireDate());
        e.setDepartment(departments.findById(r.departmentId()).orElseThrow(() -> new ResourceNotFoundException("Département introuvable")));
        if (Objects.equals(e.getId(), r.managerId()))
            throw new IllegalArgumentException("Un employé ne peut pas être son propre manager");
        e.setManager(r.managerId() == null ? null : get(r.managerId()));
    }

    public Employee get(Long id) {
        return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employé introuvable: " + id));
    }

    public EmployeeResponse response(Employee e) {
        return new EmployeeResponse(e.getId(), e.getMatricule(), e.getFirstName(), e.getLastName(), e.getEmail(), e.getPosition(), e.getHireDate(), e.getDepartment().getId(), e.getDepartment().getName(), e.getManager() == null ? null : e.getManager().getId());
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> list(Long departmentId, String search, Pageable p) {
        Page<Employee> page = departmentId != null ? employees.findByDepartmentId(departmentId, p) : search != null ? employees.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(search, search, p) : employees.findAll(p);
        return page.map(this::response);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> team(Long id) {
        get(id);
        return employees.findByManagerId(id).stream().map(this::response).toList();
    }
}
