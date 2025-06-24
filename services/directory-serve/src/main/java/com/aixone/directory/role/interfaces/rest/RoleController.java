package com.aixone.directory.role.interfaces.rest;

import java.util.UUID;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.aixone.directory.role.application.RoleApplicationService;
import com.aixone.directory.role.application.dto.AddMemberToRoleRequest;
import com.aixone.directory.role.application.dto.CreateRoleRequest;
import com.aixone.directory.role.application.dto.RoleDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants/{tenantId}/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleApplicationService roleApplicationService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@PathVariable String tenantId, @RequestBody CreateRoleRequest request) {
        RoleDto newRole = roleApplicationService.createRole(tenantId, request);
        return new ResponseEntity<>(newRole, HttpStatus.CREATED);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDto> getRole(@PathVariable UUID roleId) {
        Optional<RoleDto> role = roleApplicationService.getRole(roleId);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{roleId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMemberToRole(@PathVariable UUID roleId, @RequestBody AddMemberToRoleRequest request) {
        roleApplicationService.addMemberToRole(roleId, request);
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