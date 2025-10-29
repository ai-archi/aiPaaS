package com.aixone.directory.tenant.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Tenant {

    private final String id;
    private String name;
    private String groupId; // 租户组ID
    private TenantStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Tenant create(String name, String groupId) {
        return Tenant.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .groupId(groupId)
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateGroupId(String newGroupId) {
        this.groupId = newGroupId;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = TenantStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        // 标记为删除，实际删除由仓储层处理
        this.status = TenantStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }
} 