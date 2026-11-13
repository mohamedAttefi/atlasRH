package ma.atlasrh.controller;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ma.atlasrh.dto.*;
import ma.atlasrh.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService s;

    public EmployeeController(EmployeeService s) {
        this.s = s;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid
    @RequestBody EmployeeRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));
    }

    @GetMapping
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort) {
        return s.list(departmentId, search, PageRequest.of(page, size, parseSort(sort)));
    }

    private Sort parseSort(String sort) {
        // Swagger may serialize its array-style sort input as ["lastName"].
        String normalized = sort.replace("[", "").replace("]", "").replace("\"", "").trim();
        String[] parts = normalized.split(",", 2);
        String property = parts[0].trim();
        Set<String> sortableProperties = Set.of("id", "matricule", "firstName", "lastName", "email", "position", "hireDate");
        if (!sortableProperties.contains(property)) {
            property = "lastName";
        }
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }

    @GetMapping("/{id}/team")
    public List<EmployeeResponse> team(@PathVariable Long id) {
        return s.team(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid
    @RequestBody EmployeeRequest r) {
        return s.update(id, r);
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return s.response(s.get(id));
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
