package com.aixone.metacenter.processengine.domain;

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
 * 流程节点实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "process_nodes", indexes = {
    @Index(name = "idx_process_nodes_process_id", columnList = "process_id"),
    @Index(name = "idx_process_nodes_name", columnList = "name"),
    @Index(name = "idx_process_nodes_type", columnList = "type")
})
@EntityListeners(AuditingEntityListener.class)
public class ProcessNode {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 节点名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 节点类型 */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 节点描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 节点配置 */
    @Column(name = "config", columnDefinition = "jsonb")
    private Map<String, Object> config = new HashMap<>();

    /** 节点位置 */
    @Column(name = "position_x")
    private Integer positionX;

    /** 节点位置 */
    @Column(name = "position_y")
    private Integer positionY;

    /** 排序 */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 关联的流程 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;

    /**
     * 获取节点配置值
     * 
     * @param key 配置键
     * @return 配置值
     */
    public Object getConfigValue(String key) {
        return config.get(key);
    }

    /**
     * 设置节点配置值
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public void setConfigValue(String key, Object value) {
        config.put(key, value);
    }
} 