package com.aixone.directory.tenant.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 租户组领域聚合根
 * 支持租户分组管理，用于租户的层级组织
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class TenantGroup {

    private final String id;
    private String name;
    private String description;
    private String parentId; // 父租户组ID，支持层级结构
    private Integer sortOrder; // 排序
    private String status; // 状态：ACTIVE, INACTIVE, DELETED
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 创建租户组
     */
    public static TenantGroup create(String name, String description, String parentId) {
        return TenantGroup.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .parentId(parentId)
                .status("ACTIVE")
                .sortOrder(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新租户组名称
     */
    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新租户组描述
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新父级租户组
     */
    public void updateParent(String newParentId) {
        this.parentId = newParentId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新排序
     */
    public void updateSortOrder(Integer newSortOrder) {
        this.sortOrder = newSortOrder;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用租户组
     */
    public void deactivate() {
        this.status = "INACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活租户组
     */
    public void activate() {
        this.status = "ACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 删除租户组（软删除）
     */
    public void markAsDeleted() {
        this.status = "DELETED";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查是否为根租户组
     */
    public boolean isRoot() {
        return parentId == null || parentId.isEmpty();
    }
}

