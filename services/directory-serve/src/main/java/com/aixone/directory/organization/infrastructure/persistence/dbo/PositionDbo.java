package com.aixone.directory.organization.infrastructure.persistence.dbo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Comment;

import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "positions")
@Data
public class PositionDbo {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrganizationDbo organization;

    @Comment("租户ID")
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Comment("组织ID")
    @Column(name = "department_id", nullable = false)
    private String departmentId;

    @Comment("岗位名称")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "position_members",
            joinColumns = @JoinColumn(name = "position_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserDbo> users = new HashSet<>();
} 