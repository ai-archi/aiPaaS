package com.aixone.directory.permission.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 权限聚合根
 * 权限用于RBAC/ABAC权限决策
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    private String permissionId;
    private String tenantId;
    private String name;
    private String code;  // 权限编码（唯一）
    private String resource;  // 资源标识
    private String action;  // 操作标识（read、write、delete等）
    private PermissionType type;  // 权限类型：FUNCTIONAL/DATA
    private String description;
    private Map<String, Object> abacConditions;  // ABAC条件（JSON格式）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 权限类型枚举
     */
    public enum PermissionType {
        FUNCTIONAL,  // 功能权限
        DATA  // 数据权限
    }
    
    /**
     * 创建权限
     */
    public static Permission create(String tenantId, String name, String code, String resource, String action) {
        return Permission.builder()
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .resource(resource)
                .action(action)
                .type(PermissionType.FUNCTIONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 更新权限
     */
    public void update(String name, String code, String resource, String action, PermissionType type, String description, Map<String, Object> abacConditions) {
        this.name = name;
        this.code = code;
        this.resource = resource;
        this.action = action;
        if (type != null) {
            this.type = type;
        }
        this.description = description;
        this.abacConditions = abacConditions;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否属于指定租户
     */
    public boolean belongsToTenant(String tenantId) {
        return this.tenantId != null && this.tenantId.equals(tenantId);
    }
    
    /**
     * 获取权限标识（格式：{resource}:{action}）
     */
    public String getPermissionIdentifier() {
        return resource + ":" + action;
    }
    
    /**
     * 检查是否有ABAC条件
     */
    public boolean hasAbacConditions() {
        return abacConditions != null && !abacConditions.isEmpty();
    }
}

