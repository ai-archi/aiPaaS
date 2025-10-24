package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Topic仓储接口
 * 定义Topic聚合的持久化操作
 */
public interface TopicRepository extends Repository<Topic, Long> {
    
    /**
     * 根据名称查找Topic
     */
    Optional<Topic> findByName(String name);
    
    /**
     * 根据租户ID查找Topic
     */
    List<Topic> findByTenantId(String tenantId);
    
    /**
     * 根据状态查找Topic
     */
    List<Topic> findByStatus(Topic.TopicStatus status);
    
    /**
     * 根据租户ID和状态查找Topic
     */
    List<Topic> findByTenantIdAndStatus(String tenantId, Topic.TopicStatus status);
    
    /**
     * 根据所有者查找Topic
     */
    List<Topic> findByOwner(String owner);
    
    /**
     * 根据租户ID和所有者查找Topic
     */
    List<Topic> findByTenantIdAndOwner(String tenantId, String owner);
    
    /**
     * 检查Topic名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据名称删除Topic
     */
    void deleteByName(String name);
    
    /**
     * 统计租户的Topic数量
     */
    long countByTenantId(String tenantId);
    
    /**
     * 统计状态的Topic数量
     */
    long countByStatus(Topic.TopicStatus status);
}
