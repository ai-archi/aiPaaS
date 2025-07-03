package com.aixone.directory.group.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Group {

    private String id;
    private String tenantId;
    private String name;

    @Builder.Default
    private Set<String> members = new HashSet<>();

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    {
        System.out.println("[DEBUG][Group.@AllArgsConstructor] members=" + members + ", roles=" + roles);
    }

    public static Group create(String tenantId, String name) {
        Set<String> members = new HashSet<>();
        Set<String> roles = new HashSet<>();
        System.out.println("[DEBUG][Group.create] members=" + members + ", roles=" + roles);
        return Group.builder()
                .id(java.util.UUID.randomUUID().toString())
                .tenantId(tenantId)
                .name(name)
                .members(members)
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void addMember(String userId) {
        System.out.println("[DEBUG][Group.addMember] userId=" + userId + ", members(before)=" + this.members);
        Objects.requireNonNull(userId, "userId不能为空");
        this.members.add(userId);
        System.out.println("[DEBUG][Group.addMember] members(after)=" + this.members);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeMember(String userId) {
        System.out.println("[DEBUG][Group.removeMember] userId=" + userId + ", members(before)=" + this.members);
        Objects.requireNonNull(userId, "userId不能为空");
        this.members.remove(userId);
        System.out.println("[DEBUG][Group.removeMember] members(after)=" + this.members);
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(String roleId) {
        System.out.println("[DEBUG][Group.addRole] roleId=" + roleId + ", roles(before)=" + this.roles);
        Objects.requireNonNull(roleId, "roleId不能为空");
        this.roles.add(roleId);
        System.out.println("[DEBUG][Group.addRole] roles(after)=" + this.roles);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(String roleId) {
        System.out.println("[DEBUG][Group.removeRole] roleId=" + roleId + ", roles(before)=" + this.roles);
        Objects.requireNonNull(roleId, "roleId不能为空");
        this.roles.remove(roleId);
        System.out.println("[DEBUG][Group.removeRole] roles(after)=" + this.roles);
        this.updatedAt = LocalDateTime.now();
    }
} 