package com.aixone.directory.organization.domain.aggregate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 组织聚合根
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Organization {

    private final UUID id;
    private final UUID tenantId;
    private String name;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<Department> departments = new HashSet<>();

    @Builder.Default
    private Set<Position> positions = new HashSet<>();

    /**
     * 创建一个新的组织。
     *
     * @param tenantId 租户ID
     * @param name     组织名称
     * @return Organization 实例
     */
    public static Organization create(UUID tenantId, String name) {
        return Organization.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .departments(new HashSet<>())
                .positions(new HashSet<>())
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public Department addDepartment(String name, UUID parentId) {
        Department newDepartment = Department.create(this.tenantId, this.id, name, parentId);
        this.departments.add(newDepartment);
        this.updatedAt = LocalDateTime.now();
        return newDepartment;
    }

    public void removeDepartment(UUID departmentId) {
        this.departments.removeIf(department -> department.getId().equals(departmentId));
        this.updatedAt = LocalDateTime.now();
    }

    public Position addPosition(String name) {
        Position newPosition = Position.create(this.tenantId, this.id, name);
        this.positions.add(newPosition);
        this.updatedAt = LocalDateTime.now();
        return newPosition;
    }

    public void removePosition(UUID positionId) {
        this.positions.removeIf(position -> position.getId().equals(positionId));
        this.updatedAt = LocalDateTime.now();
    }
} 