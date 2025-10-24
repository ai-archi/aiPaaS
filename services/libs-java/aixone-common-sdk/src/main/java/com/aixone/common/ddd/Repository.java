package com.aixone.common.ddd;

import java.util.List;
import java.util.Optional;

/**
 * 仓储接口基类
 * 定义通用的仓储操作方法
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 * @param <T> 实体类型
 * @param <ID> 实体标识类型
 */
public interface Repository<T, ID> {
    
    /**
     * 保存实体
     * 
     * @param entity 实体
     * @return 保存后的实体
     */
    T save(T entity);
    
    /**
     * 根据ID查找实体
     * 
     * @param id 实体ID
     * @return 实体Optional
     */
    Optional<T> findById(ID id);
    
    /**
     * 根据ID查找实体（不返回Optional）
     * 
     * @param id 实体ID
     * @return 实体，如果不存在返回null
     */
    T getById(ID id);
    
    /**
     * 查找所有实体
     * 
     * @return 实体列表
     */
    List<T> findAll();
    
    /**
     * 根据租户ID查找所有实体
     * 
     * @param tenantId 租户ID
     * @return 实体列表
     */
    List<T> findByTenantId(String tenantId);
    
    /**
     * 删除实体
     * 
     * @param entity 实体
     */
    void delete(T entity);
    
    /**
     * 根据ID删除实体
     * 
     * @param id 实体ID
     */
    void deleteById(ID id);
    
    /**
     * 检查实体是否存在
     * 
     * @param id 实体ID
     * @return 是否存在
     */
    boolean existsById(ID id);
    
    /**
     * 统计实体数量
     * 
     * @return 实体数量
     */
    long count();
    
    /**
     * 根据租户ID统计实体数量
     * 
     * @param tenantId 租户ID
     * @return 实体数量
     */
    long countByTenantId(String tenantId);
}
