package com.aixone.directory.role.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Role {

    private final UUID id;
    private final UUID tenantId;
    private String name;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creating a new role
    public Role(UUID tenantId, String name) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for reconstructing from persistence
    public Role(UUID id, UUID tenantId, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Role create(UUID tenantId, String name) {
        return new Role(tenantId, name);
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }
} 