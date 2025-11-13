package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.RolePermissionDbo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限关系仓储实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class PostgresRolePermissionRepository implements RolePermissionRepository {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void assignPermission(String roleId, String permissionId, String tenantId) {
        RolePermissionDbo dbo = new RolePermissionDbo(roleId, permissionId, tenantId, LocalDateTime.now());
        rolePermissionJpaRepository.save(dbo);
    }

    @Override
    public void removePermission(String roleId, String permissionId) {
        rolePermissionJpaRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);
    }

    @Override
    public List<Permission> findPermissionsByRoleId(String roleId, String tenantId) {
        List<String> permissionIds = rolePermissionJpaRepository.findPermissionIdsByRoleIdAndTenantId(roleId, tenantId);
        return permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findPermissionIdsByRoleId(String roleId, String tenantId) {
        return rolePermissionJpaRepository.findPermissionIdsByRoleIdAndTenantId(roleId, tenantId);
    }

    @Override
    public void assignPermissions(String roleId, List<String> permissionIds, String tenantId) {
        List<RolePermissionDbo> dbos = permissionIds.stream()
                .map(permissionId -> new RolePermissionDbo(roleId, permissionId, tenantId, LocalDateTime.now()))
                .collect(Collectors.toList());
        rolePermissionJpaRepository.saveAll(dbos);
    }

    @Override
    public void removePermissions(String roleId, List<String> permissionIds) {
        rolePermissionJpaRepository.deleteByRoleIdAndPermissionIds(roleId, permissionIds);
    }

    @Override
    public void removeAllPermissions(String roleId, String tenantId) {
        rolePermissionJpaRepository.deleteByRoleIdAndTenantId(roleId, tenantId);
    }

    @Override
    public boolean hasPermission(String roleId, String permissionId) {
        return rolePermissionJpaRepository.existsByRoleIdAndPermissionId(roleId, permissionId);
    }
}

