package ma.atlasrh.controller;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
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
    public Page<EmployeeResponse> list(@RequestParam(required = false) Long departmentId,
                                       @RequestParam(required = false) String search, @PageableDefault(
        size = 20,
        sort = "lastName"
    ) Pageable p) {
        return s.list(departmentId, search, p);
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
