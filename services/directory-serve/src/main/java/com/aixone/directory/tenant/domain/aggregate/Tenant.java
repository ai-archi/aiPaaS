package com.aixone.directory.tenant.domain.aggregate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Tenant {

    private final String id;
    private String name;
    private TenantStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Tenant create(String name) {
        return Tenant.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
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
} 