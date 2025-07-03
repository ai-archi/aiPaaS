package com.aixone.directory.user.infrastructure.persistence.dbo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.aixone.directory.user.domain.aggregate.UserStatus;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
public class UserDbo {

    @Id
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = false)
    private String username;

    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_departments",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<DepartmentDbo> departments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_positions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private Set<PositionDbo> positions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleDbo> roles = new HashSet<>();
} 