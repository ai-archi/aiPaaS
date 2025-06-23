package com.aixone.directory.role.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Role {

    private final UUID id;
    private final UUID tenantId;
    private String name;

    @Builder.Default
    private Set<UUID> members = new HashSet<>();

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Role create(UUID tenantId, String name) {
        return Role.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .members(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void addMember(UUID userId) {
        this.members.add(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeMember(UUID userId) {
        this.members.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }
} 