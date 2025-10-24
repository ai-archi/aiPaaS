package com.aixone.tech.auth.authorization.domain.repository;

import com.aixone.tech.auth.authorization.domain.model.UserRole;
import java.util.List;

/**
 * 用户角色关联仓储接口
 */
public interface UserRoleRepository {
    UserRole save(UserRole userRole);
    List<UserRole> findByTenantIdAndUserId(String tenantId, String userId);
    List<UserRole> findByTenantIdAndRoleId(String tenantId, String roleId);
    void deleteByTenantIdAndUserId(String tenantId, String userId);
    void deleteByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId);
    boolean existsByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId);
}
