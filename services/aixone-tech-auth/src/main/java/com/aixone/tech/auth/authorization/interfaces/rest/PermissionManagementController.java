package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.dto.CreatePermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.PermissionResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdatePermissionRequest;
import com.aixone.tech.auth.authorization.application.service.PermissionManagementApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
public class PermissionManagementController {

    private final PermissionManagementApplicationService permissionManagementApplicationService;

    public PermissionManagementController(PermissionManagementApplicationService permissionManagementApplicationService) {
        this.permissionManagementApplicationService = permissionManagementApplicationService;
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionManagementApplicationService.createPermission(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable String permissionId,
                                                              @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionResponse response = permissionManagementApplicationService.updatePermission(permissionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tenantId}/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable String tenantId,
                                                 @PathVariable String permissionId) {
        permissionManagementApplicationService.deletePermission(tenantId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tenantId}/{permissionId}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable String tenantId,
                                                               @PathVariable String permissionId) {
        PermissionResponse response = permissionManagementApplicationService.getPermissionById(tenantId, permissionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions(@PathVariable String tenantId) {
        List<PermissionResponse> response = permissionManagementApplicationService.getAllPermissions(tenantId);
        return ResponseEntity.ok(response);
    }
}
