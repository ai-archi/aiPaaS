package com.aixone.directory.organization.infrastructure.persistence.dbo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Comment;

import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;

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
@Table(name = "departments")
@Data
public class DepartmentDbo {

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

    @Comment("部门名称")
    @Column(nullable = false)
    private String name;

    @Comment("上级部门ID")
    @Column(name = "parent_id")
    private String parentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "department_members",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserDbo> users = new HashSet<>();
} 