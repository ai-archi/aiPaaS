package com.aixone.auth.controller;

import com.aixone.auth.service.PermissionServiceAdapter;
import com.aixone.permission.model.Role;
import com.aixone.permission.model.Permission;
import com.aixone.auth.common.ApiResponse;
import com.aixone.auth.common.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "权限API", description = "统一权限服务API")
@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {
    @Autowired
    private PermissionServiceAdapter permissionServiceAdapter;

    /** 校验用户是否有某权限 */
    @Operation(summary = "校验用户权限")
    @PostMapping("/check-access")
    public ApiResponse<?> checkAccess(@RequestParam String userId, @RequestParam String resource, @RequestParam String action) {
        boolean allowed = permissionServiceAdapter.checkAccess(userId, resource, action, new HashMap<>());
        return allowed ? ApiResponse.success("有权限") : ApiResponse.error(ErrorCode.FORBIDDEN, "无权限");
    }

    /** 获取用户所有角色 */
    @Operation(summary = "获取用户所有角色")
    @GetMapping("/user-roles")
    public ApiResponse<?> getUserRoles(@RequestParam String userId) {
        List<Role> roles = permissionServiceAdapter.getUserRoles(userId);
        return ApiResponse.success(roles);
    }

    /** 获取角色所有权限 */
    @Operation(summary = "获取角色所有权限")
    @GetMapping("/role-permissions")
    public ApiResponse<?> getRolePermissions(@RequestParam String roleId) {
        List<Permission> perms = permissionServiceAdapter.getRolePermissions(roleId);
        return ApiResponse.success(perms);
    }
} 