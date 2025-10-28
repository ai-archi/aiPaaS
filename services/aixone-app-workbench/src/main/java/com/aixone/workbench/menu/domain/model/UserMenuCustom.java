package com.aixone.workbench.menu.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户菜单个性化配置实体
 * 存储用户对特定菜单的个性化配置，如顺序、隐藏、快捷入口等
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "wb_user_menu_custom", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "menu_id"}, name = "uk_user_menu")
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_tenant", columnList = "user_id, tenant_id")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserMenuCustom implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;
    
    /**
     * 租户ID
     */
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;
    
    /**
     * 菜单ID
     */
    @Column(name = "menu_id", nullable = false, updatable = false)
    private UUID menuId;
    
    /**
     * 个性化配置（JSON格式）
     * 包含：顺序、隐藏、快捷入口等配置
     */
    @Column(name = "config", columnDefinition = "TEXT")
    private String config;
    
    /**
     * 是否在快捷入口中显示
     */
    @Column(name = "is_quick_entry")
    private Boolean isQuickEntry = false;
    
    /**
     * 自定义顺序（覆盖菜单的默认顺序）
     */
    @Column(name = "custom_order")
    private Integer customOrder;
    
    /**
     * 是否隐藏该菜单（用户自定义隐藏）
     */
    @Column(name = "is_hidden")
    private Boolean isHidden = false;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 业务规则：判断菜单是否被用户隐藏
     */
    public boolean isHidden() {
        return isHidden != null && isHidden;
    }
    
    /**
     * 业务规则：判断是否为快捷入口
     */
    public boolean isQuickEntry() {
        return isQuickEntry != null && isQuickEntry;
    }
}
