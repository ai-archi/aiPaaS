package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.command.AssignUserRoleCommand;
import com.aixone.tech.auth.authorization.application.dto.AssignUserRoleRequest;
import com.aixone.tech.auth.authorization.application.dto.UserRoleResponse;
import com.aixone.tech.auth.authorization.application.service.UserRoleManagementApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色管理控制器
 */
@RestController
@RequestMapping("/admin")
public class UserRoleManagementController {

    private final UserRoleManagementApplicationService userRoleManagementService;

    public UserRoleManagementController(UserRoleManagementApplicationService userRoleManagementService) {
        this.userRoleManagementService = userRoleManagementService;
    }

    /**
     * 分配用户角色
     */
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<UserRoleResponse> assignUserRole(
            @PathVariable String userId,
            @Valid @RequestBody AssignUserRoleRequest request) {
        
        // 确保路径参数和请求体中的用户ID一致
        if (!userId.equals(request.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        
        AssignUserRoleCommand command = new AssignUserRoleCommand(
            request.getTenantId(),
            request.getUserId(),
            request.getRoleId()
        );
        
        UserRoleResponse response = userRoleManagementService.assignUserRole(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeUserRole(
            @PathVariable String userId,
            @PathVariable String roleId,
            @RequestParam String tenantId) {
        
        userRoleManagementService.removeUserRole(tenantId, userId, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询用户的所有角色
     */
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<UserRoleResponse>> getUserRoles(
            @PathVariable String userId,
            @RequestParam String tenantId) {
        
        List<UserRoleResponse> userRoles = userRoleManagementService.getUserRoles(tenantId, userId);
        return ResponseEntity.ok(userRoles);
    }

    /**
     * 检查用户是否拥有指定角色
     */
    @GetMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<Boolean> hasRole(
            @PathVariable String userId,
            @PathVariable String roleId,
            @RequestParam String tenantId) {
        
        boolean hasRole = userRoleManagementService.hasRole(tenantId, userId, roleId);
        return ResponseEntity.ok(hasRole);
    }
}
