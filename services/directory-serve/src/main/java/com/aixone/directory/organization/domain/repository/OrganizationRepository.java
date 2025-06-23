package com.aixone.directory.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.aixone.directory.organization.domain.aggregate.Organization;

/**
 * 组织聚合的仓储接口
 */
public interface OrganizationRepository {

    /**
     * 保存组织聚合（包括其下的部门和岗位）。
     *
     * @param organization 组织聚合实例
     */
    void save(Organization organization);

    /**
     * 根据ID查找组织。
     *
     * @param id 组织ID
     * @return 可选的组织实例
     */
    Optional<Organization> findById(UUID id);

    /**
     * 根据租户ID和组织名称查找组织。
     *
     * @param tenantId 租户ID
     * @param name     组织名称
     * @return 可选的组织实例
     */
    Optional<Organization> findByTenantIdAndName(UUID tenantId, String name);

    /**
     * 根据ID删除组织。
     *
     * @param id 组织ID
     */
    void deleteById(UUID id);
} 