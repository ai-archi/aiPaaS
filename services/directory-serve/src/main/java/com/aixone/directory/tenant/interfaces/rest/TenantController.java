package com.aixone.directory.tenant.interfaces.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.tenant.application.TenantApplicationService;
import com.aixone.directory.tenant.application.TenantDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantApplicationService tenantApplicationService;

    @PostMapping
    public ResponseEntity<UUID> createTenant(@RequestBody TenantDto.CreateTenantCommand command) {
        UUID tenantId = tenantApplicationService.createTenant(command);
        return ResponseEntity.status(201).body(tenantId);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDto.TenantView> getTenantById(@PathVariable UUID tenantId) {
        return tenantApplicationService.findTenantById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 