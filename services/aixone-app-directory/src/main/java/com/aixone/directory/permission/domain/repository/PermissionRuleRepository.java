package com.aixone.directory.permission.domain.repository;

import com.aixone.directory.permission.domain.aggregate.PermissionRule;

import java.util.List;
import java.util.Optional;

/**
 * 权限规则仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface PermissionRuleRepository {
    
    /**
     * 保存权限规则
     */
    PermissionRule save(PermissionRule permissionRule);
    
    /**
     * 根据ID查找权限规则
     */
    Optional<PermissionRule> findById(String id);
    
    /**
     * 根据租户ID查找所有权限规则
     */
    List<PermissionRule> findByTenantId(String tenantId);
    
    /**
     * 根据路径和方法查找匹配的权限规则
     * 返回匹配的权限规则列表，按优先级降序排序
     */
    List<PermissionRule> findByPathAndMethod(String tenantId, String path, String method);
    
    /**
     * 删除权限规则
     */
    void delete(String id);
    
    /**
     * 检查是否存在
     */
    boolean existsById(String id);
}

