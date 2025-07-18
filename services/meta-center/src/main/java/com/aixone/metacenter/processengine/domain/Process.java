package com.aixone.metacenter.processengine.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "processes", indexes = {
    @Index(name = "idx_processes_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_processes_name", columnList = "name"),
    @Index(name = "idx_processes_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Process {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流程名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 显示名称 */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** 流程类型 */
    @Column(name = "process_type", nullable = false, length = 50)
    private String processType;

    /** 流程描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 流程定义 */
    @Column(name = "definition", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> definition = new HashMap<>();

    /** 流程状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "draft";

    /** 版本号 */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /** 更新时间 */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

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

    /** 流程节点列表 */
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessNode> nodes = new ArrayList<>();

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, Object> definition) {
        this.definition = definition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProcessNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ProcessNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * 添加流程节点
     * 
     * @param node 流程节点
     */
    public void addNode(ProcessNode node) {
        nodes.add(node);
        node.setProcess(this);
    }

    /**
     * 移除流程节点
     * 
     * @param node 流程节点
     */
    public void removeNode(ProcessNode node) {
        nodes.remove(node);
        node.setProcess(null);
    }

    /**
     * 检查是否为草稿状态
     * 
     * @return 是否为草稿状态
     */
    public boolean isDraft() {
        return "draft".equals(status);
    }

    /**
     * 检查是否为已发布状态
     * 
     * @return 是否为已发布状态
     */
    public boolean isPublished() {
        return "published".equals(status);
    }

    /**
     * 增加版本号
     */
    public void incrementVersion() {
        this.version++;
    }
} 