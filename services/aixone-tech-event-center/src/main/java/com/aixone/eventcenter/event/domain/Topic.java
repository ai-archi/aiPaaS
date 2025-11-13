package com.aixone.eventcenter.event.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Date;

/**
 * Topic聚合根
 * 表示Kafka Topic的元数据和管理信息
 */
@jakarta.persistence.Entity
@Table(name = "topics")
@EqualsAndHashCode(callSuper = true)
public class Topic extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;
    
    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;
    
    @Column(name = "owner", nullable = false, length = 100)
    private String owner;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    private TopicStatus status;
    
    @Column(name = "tenant_id", length = 50)
    private String tenantId;
    
    @Column(name = "create_time", nullable = false)
    private Instant createTime;
    
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;
    
    @Column(name = "partition_count")
    private Integer partitionCount = 1;
    
    @Column(name = "replication_factor")
    private Short replicationFactor = 1;
    
    // 默认构造函数
    public Topic() {
        super(0L);
    }
    
    // 业务构造函数
    public Topic(String name, String owner, String description, String tenantId) {
        super(0L);
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.tenantId = tenantId;
        this.status = TopicStatus.ACTIVE;
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
    }
    
    /**
     * 激活Topic
     */
    public void activate() {
        this.status = TopicStatus.ACTIVE;
        this.updateTime = Instant.now();
    }
    
    /**
     * 停用Topic
     */
    public void deactivate() {
        this.status = TopicStatus.INACTIVE;
        this.updateTime = Instant.now();
    }
    
    /**
     * 更新描述
     */
    public void updateDescription(String description) {
        this.description = description;
        this.updateTime = Instant.now();
    }
    
    @Override
    public Long getId() {
        return topicId;
    }
    
    // Getters and Setters
    public Long getTopicId() {
        return topicId;
    }
    
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TopicStatus getStatus() {
        return status;
    }
    
    public void setStatus(TopicStatus status) {
        this.status = status;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public Instant getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }
    
    public Instant getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
    
    public Integer getPartitionCount() {
        return partitionCount;
    }
    
    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }
    
    public Short getReplicationFactor() {
        return replicationFactor;
    }
    
    public void setReplicationFactor(Short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
    
    /**
     * Topic状态枚举
     */
    public enum TopicStatus {
        ACTIVE, INACTIVE, PENDING, ERROR
    }
}
