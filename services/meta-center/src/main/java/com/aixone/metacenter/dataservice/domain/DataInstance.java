package com.aixone.metacenter.dataservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据实例实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "data_instances", indexes = {
    @Index(name = "idx_data_instances_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_data_instances_meta_object_id", columnList = "meta_object_id"),
    @Index(name = "idx_data_instances_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class DataInstance {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 元数据对象ID */
    @Column(name = "meta_object_id", nullable = false)
    private Long metaObjectId;

    /** 数据内容 */
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> data = new HashMap<>();

    /** 数据状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";

    /** 数据版本 */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建人 */
    @Column(name = "created_by", length = 100)
    private String createdBy;

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新人 */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 获取数据字段值
     * 
     * @param fieldName 字段名
     * @return 字段值
     */
    public Object getFieldValue(String fieldName) {
        return data.get(fieldName);
    }

    /**
     * 设置数据字段值
     * 
     * @param fieldName 字段名
     * @param value 字段值
     */
    public void setFieldValue(String fieldName, Object value) {
        data.put(fieldName, value);
    }

    /**
     * 检查数据是否激活
     * 
     * @return 是否激活
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 增加版本号
     */
    public void incrementVersion() {
        this.version++;
    }
} 