package com.aixone.directory.role.interfaces.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.role.application.RoleApplicationService;
import com.aixone.directory.role.application.dto.AddMemberToRoleRequest;
import com.aixone.directory.role.application.dto.CreateRoleRequest;
import com.aixone.directory.role.application.dto.RoleDto;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/roles")
public class RoleController {

    private final RoleApplicationService roleApplicationService;

    public RoleController(RoleApplicationService roleApplicationService) {
        this.roleApplicationService = roleApplicationService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@PathVariable UUID tenantId, @RequestBody CreateRoleRequest request) {
        RoleDto newRole = roleApplicationService.createRole(tenantId, request);
        return new ResponseEntity<>(newRole, HttpStatus.CREATED);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDto> getRole(@PathVariable UUID tenantId, @PathVariable UUID roleId) {
        RoleDto role = roleApplicationService.getRole(tenantId, roleId);
        return ResponseEntity.ok(role);
    }

    @PostMapping("/{roleId}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID roleId,
            @RequestBody AddMemberToRoleRequest request) {
        roleApplicationService.addMemberToRole(tenantId, roleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{roleId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID roleId,
            @PathVariable UUID userId) {
        roleApplicationService.removeMemberFromRole(tenantId, roleId, userId);
        return ResponseEntity.noContent().build();
    }
} 