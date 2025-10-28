package com.aixone.workbench.dashboard.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户仪表盘聚合根
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "wb_user_dashboard", indexes = {
    @Index(name = "idx_dashboard_user_id", columnList = "user_id"),
    @Index(name = "idx_dashboard_user_tenant", columnList = "user_id, tenant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Dashboard implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "layout", columnDefinition = "JSONB")
    private String layout;
    
    @Column(name = "components", columnDefinition = "JSONB")
    private String components;
    
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

