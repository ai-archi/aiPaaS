package com.aixone.workbench.quickentry.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户快捷入口实体
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "wb_user_quick_entry", indexes = {
    @Index(name = "idx_quick_entry_user_id", columnList = "user_id"),
    @Index(name = "idx_quick_entry_user_tenant", columnList = "user_id, tenant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QuickEntry implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "entry_id", nullable = false)
    private UUID entryId;
    
    @Column(name = "menu_id")
    private UUID menuId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

