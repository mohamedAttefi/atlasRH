package ma.atlasrh.controller;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import ma.atlasrh.dto.*;
import ma.atlasrh.entity.LeaveStatus;
import ma.atlasrh.service.LeaveService;

@RestController
@RequestMapping("/api")
public class LeaveController {
    private final LeaveService s;

    public LeaveController(LeaveService s) {
        this.s = s;
    }

    @GetMapping("/employees/{employeeId}/leave-balance")
    public List<Map<String, Object>> balance(@PathVariable Long employeeId) {
        return s.balances(employeeId);
    }

    @PostMapping("/employees/{employeeId}/leave-requests/{id}/cancel")
    public LeaveResponse cancel(@PathVariable Long employeeId, @PathVariable Long id) {
        return s.cancel(employeeId, id);
    }

    @PostMapping("/leave-requests")
    public ResponseEntity<LeaveResponse> create(@Valid
    @RequestBody LeaveRequestCreate r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));
    }

    @PostMapping("/managers/{managerId}/leave-requests/{id}/decision")
    public LeaveResponse decide(@PathVariable Long managerId, @PathVariable Long id, @Valid
    @RequestBody LeaveDecision d) {
        return s.decide(managerId, id, d);
    }

    @GetMapping("/employees/{employeeId}/leave-requests")
    public List<LeaveResponse> mine(@PathVariable Long employeeId, @RequestParam(required = false) LeaveStatus status) {
        return s.mine(employeeId, status);
    }

    @GetMapping("/managers/{managerId}/leave-requests/pending")
    public List<LeaveResponse> pending(@PathVariable Long managerId) {
        return s.pendingTeam(managerId);
    }

    @GetMapping("/employees/{employeeId}/seniority")
    public Map<String, Object> seniority(@PathVariable Long employeeId) {
        return s.seniority(employeeId);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
