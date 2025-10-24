package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.dto.AbacPolicyResponse;
import com.aixone.tech.auth.authorization.application.dto.CreateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.application.dto.UpdateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.application.service.AbacPolicyManagementApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/abac-policies")
public class AbacPolicyManagementController {

    private final AbacPolicyManagementApplicationService abacPolicyManagementApplicationService;

    public AbacPolicyManagementController(AbacPolicyManagementApplicationService abacPolicyManagementApplicationService) {
        this.abacPolicyManagementApplicationService = abacPolicyManagementApplicationService;
    }

    @PostMapping
    public ResponseEntity<AbacPolicyResponse> createAbacPolicy(@Valid @RequestBody CreateAbacPolicyRequest request) {
        AbacPolicyResponse response = abacPolicyManagementApplicationService.createAbacPolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<AbacPolicyResponse> updateAbacPolicy(@PathVariable String policyId,
                                                               @Valid @RequestBody UpdateAbacPolicyRequest request) {
        AbacPolicyResponse response = abacPolicyManagementApplicationService.updateAbacPolicy(policyId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tenantId}/{policyId}")
    public ResponseEntity<Void> deleteAbacPolicy(@PathVariable String tenantId,
                                                 @PathVariable String policyId) {
        abacPolicyManagementApplicationService.deleteAbacPolicy(tenantId, policyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tenantId}/{policyId}")
    public ResponseEntity<AbacPolicyResponse> getAbacPolicyById(@PathVariable String tenantId,
                                                                @PathVariable String policyId) {
        AbacPolicyResponse response = abacPolicyManagementApplicationService.getAbacPolicyById(tenantId, policyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<AbacPolicyResponse>> getAllAbacPolicies(@PathVariable String tenantId) {
        List<AbacPolicyResponse> response = abacPolicyManagementApplicationService.getAllAbacPolicies(tenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tenantId}/resource/{resource}/action/{action}")
    public ResponseEntity<List<AbacPolicyResponse>> getAbacPoliciesByResourceAndAction(
            @PathVariable String tenantId,
            @PathVariable String resource,
            @PathVariable String action) {
        List<AbacPolicyResponse> response = abacPolicyManagementApplicationService.getAbacPoliciesByResourceAndAction(tenantId, resource, action);
        return ResponseEntity.ok(response);
    }
}
