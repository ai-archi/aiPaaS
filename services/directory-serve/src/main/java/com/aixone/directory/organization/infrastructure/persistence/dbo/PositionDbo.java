package com.aixone.directory.organization.infrastructure.persistence.dbo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "positions")
@Data
public class PositionDbo {

    @Id
    private UUID id;

    @Comment("租户ID")
    @Column(nullable = false)
    private UUID tenantId;

    @Comment("组织ID")
    @Column(nullable = false)
    private UUID orgId;

    @Comment("岗位名称")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", insertable = false, updatable = false)
    private OrganizationDbo organization;
} 