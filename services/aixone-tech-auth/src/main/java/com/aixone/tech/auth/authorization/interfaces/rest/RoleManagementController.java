package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.dto.CreateRoleRequest;
import com.aixone.tech.auth.authorization.application.dto.RoleResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdateRoleRequest;
import com.aixone.tech.auth.authorization.application.service.RoleManagementApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/admin")
public class RoleManagementController {

    private final RoleManagementApplicationService roleManagementService;

    public RoleManagementController(RoleManagementApplicationService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * 创建角色
     */
    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleManagementService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新角色
     */
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable String roleId,
            @RequestParam String tenantId,
            @Valid @RequestBody UpdateRoleRequest request) {
        
        RoleResponse response = roleManagementService.updateRole(tenantId, roleId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String roleId,
            @RequestParam String tenantId) {
        
        roleManagementService.deleteRole(tenantId, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> getRole(
            @PathVariable String roleId,
            @RequestParam String tenantId) {
        
        RoleResponse response = roleManagementService.getRole(tenantId, roleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询租户下的所有角色
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles(@RequestParam String tenantId) {
        List<RoleResponse> roles = roleManagementService.getRoles(tenantId);
        return ResponseEntity.ok(roles);
    }
}
