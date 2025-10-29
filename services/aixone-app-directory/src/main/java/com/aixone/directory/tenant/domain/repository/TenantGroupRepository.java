package com.aixone.directory.tenant.domain.repository;

import com.aixone.directory.tenant.domain.aggregate.TenantGroup;

import java.util.List;
import java.util.Optional;

/**
 * 租户组仓储接口
 */
public interface TenantGroupRepository {

    /**
     * 保存租户组
     */
    TenantGroup save(TenantGroup tenantGroup);

    /**
     * 根据ID查找租户组
     */
    Optional<TenantGroup> findById(String id);

    /**
     * 查找所有租户组
     */
    List<TenantGroup> findAll();

    /**
     * 根据父ID查找租户组
     */
    List<TenantGroup> findByParentId(String parentId);

    /**
     * 查找根租户组
     */
    List<TenantGroup> findRootGroups();

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 删除租户组
     */
    void deleteById(String id);
}

