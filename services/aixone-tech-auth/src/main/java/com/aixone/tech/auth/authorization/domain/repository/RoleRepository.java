package com.aixone.tech.auth.authorization.domain.repository;

import com.aixone.tech.auth.authorization.domain.model.Role;
import java.util.List;

/**
 * 角色仓储接口
 */
public interface RoleRepository {
    Role save(Role role);
    Role findByTenantIdAndRoleId(String tenantId, String roleId);
    List<Role> findByTenantIdAndRoleIdIn(String tenantId, List<String> roleIds);
    List<Role> findByTenantId(String tenantId);
    void deleteByTenantIdAndRoleId(String tenantId, String roleId);
    boolean existsByTenantIdAndName(String tenantId, String name);
}
