package ma.atlasrh.controller;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ma.atlasrh.dto.*;
import ma.atlasrh.service.ContractService;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    private final ContractService s;

    public ContractController(ContractService s) {
        this.s = s;
    }

    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid
    @RequestBody ContractRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));
    }

    @PostMapping("/{id}/end")
    public ContractResponse end(@PathVariable Long id) {
        return s.end(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<ContractResponse> history(@PathVariable Long employeeId) {
        return s.history(employeeId);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
