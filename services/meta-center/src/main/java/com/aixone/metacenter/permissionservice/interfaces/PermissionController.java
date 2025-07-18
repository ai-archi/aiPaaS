package com.aixone.metacenter.permissionservice.interfaces;

import com.aixone.metacenter.common.response.ApiResponse;
import com.aixone.metacenter.permissionservice.application.PermissionApplicationService;
import com.aixone.metacenter.permissionservice.application.dto.PermissionDTO;
import com.aixone.metacenter.permissionservice.application.dto.PermissionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    @Autowired
    private PermissionApplicationService permissionApplicationService;

    /**
     * 创建权限
     * 
     * @param permissionDTO 权限DTO
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<PermissionDTO> createPermission(@RequestBody PermissionDTO permissionDTO) {
        try {
            PermissionDTO created = permissionApplicationService.createPermission(permissionDTO);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error("创建权限失败: " + e.getMessage());
        }
    }

    /**
     * 更新权限
     * 
     * @param id 权限ID
     * @param permissionDTO 权限DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<PermissionDTO> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
        try {
            PermissionDTO updated = permissionApplicationService.updatePermission(id, permissionDTO);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error("更新权限失败: " + e.getMessage());
        }
    }

    /**
     * 删除权限
     * 
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        try {
            permissionApplicationService.deletePermission(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除权限失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取权限
     * 
     * @param id 权限ID
     * @return 权限
     */
    @GetMapping("/{id}")
    public ApiResponse<PermissionDTO> getPermissionById(@PathVariable Long id) {
        try {
            PermissionDTO permission = permissionApplicationService.getPermissionById(id);
            return ApiResponse.success(permission);
        } catch (Exception e) {
            return ApiResponse.error("获取权限失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户ID获取权限列表
     * 
     * @param tenantId 租户ID
     * @return 权限列表
     */
    @GetMapping("/by-tenant/{tenantId}")
    public ApiResponse<List<PermissionDTO>> getPermissionsByTenantId(@PathVariable String tenantId) {
        try {
            List<PermissionDTO> permissions = permissionApplicationService.getPermissionsByTenantId(tenantId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error("获取权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据角色ID获取权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/by-role/{roleId}")
    public ApiResponse<List<PermissionDTO>> getPermissionsByRoleId(@PathVariable Long roleId) {
        try {
            List<PermissionDTO> permissions = permissionApplicationService.getPermissionsByRoleId(roleId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error("获取权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/by-user/{userId}")
    public ApiResponse<List<PermissionDTO>> getPermissionsByUserId(@PathVariable Long userId) {
        try {
            List<PermissionDTO> permissions = permissionApplicationService.getPermissionsByUserId(userId);
            return ApiResponse.success(permissions);
        } catch (Exception e) {
            return ApiResponse.error("获取权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询权限
     * 
     * @param query 查询条件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @PostMapping("/search")
    public ApiResponse<Page<PermissionDTO>> searchPermissions(
            @RequestBody PermissionQuery query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<PermissionDTO> result = permissionApplicationService.getPermissions(query, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("查询权限失败: " + e.getMessage());
        }
    }
} 