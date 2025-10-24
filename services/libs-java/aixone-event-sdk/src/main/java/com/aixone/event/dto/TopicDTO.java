package com.aixone.event.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Topic数据传输对象
 * 基于事件中心的Topic实体设计，提供Topic相关的核心数据
 */
public class TopicDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long topicId;
    private String name;
    private String owner;
    private String description;
    private String status;
    private String tenantId;
    private Instant createTime;
    private Instant updateTime;
    private Integer partitionCount = 1;
    private Short replicationFactor = 1;

    // 默认构造函数
    public TopicDTO() {}

    // 业务构造函数
    public TopicDTO(String name, String owner, String description, String tenantId) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.tenantId = tenantId;
        this.status = "ACTIVE";
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
    }

    // Getters and Setters
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    
    public Instant getUpdateTime() { return updateTime; }
    public void setUpdateTime(Instant updateTime) { this.updateTime = updateTime; }
    
    public Integer getPartitionCount() { return partitionCount; }
    public void setPartitionCount(Integer partitionCount) { this.partitionCount = partitionCount; }
    
    public Short getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(Short replicationFactor) { this.replicationFactor = replicationFactor; }

    @Override
    public String toString() {
        return "TopicDTO{" +
                "topicId=" + topicId +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", status='" + status + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
