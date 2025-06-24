package com.aixone.directory.group.domain.aggregate;

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
public class Group {

    private final UUID id;
    private final UUID tenantId;
    private String name;

    @Builder.Default
    private Set<UUID> members = new HashSet<>();

    @Builder.Default
    private Set<UUID> roles = new HashSet<>();

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Group create(UUID tenantId, String name) {
        return Group.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .members(new HashSet<>())
                .roles(new HashSet<>())
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

    public void addRole(UUID roleId) {
        this.roles.add(roleId);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(UUID roleId) {
        this.roles.remove(roleId);
        this.updatedAt = LocalDateTime.now();
    }
} 