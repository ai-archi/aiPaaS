package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.infrastructure.persistence.dbo.RolePermissionDbo;
import com.aixone.directory.permission.infrastructure.persistence.dbo.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关系 JPA 仓储
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionDbo, RolePermissionId> {
    
    /**
     * 根据角色ID和租户ID查找权限ID列表
     */
    @Query("SELECT rp.permissionId FROM RolePermissionDbo rp WHERE rp.roleId = :roleId AND rp.tenantId = :tenantId")
    List<String> findPermissionIdsByRoleIdAndTenantId(@Param("roleId") String roleId, @Param("tenantId") String tenantId);
    
    /**
     * 检查角色是否拥有权限
     */
    @Query("SELECT COUNT(rp) > 0 FROM RolePermissionDbo rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
    
    /**
     * 删除角色的所有权限
     */
    @Modifying
    @Query("DELETE FROM RolePermissionDbo rp WHERE rp.roleId = :roleId AND rp.tenantId = :tenantId")
    void deleteByRoleIdAndTenantId(@Param("roleId") String roleId, @Param("tenantId") String tenantId);
    
    /**
     * 删除角色的指定权限
     */
    @Modifying
    @Query("DELETE FROM RolePermissionDbo rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    void deleteByRoleIdAndPermissionId(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
    
    /**
     * 批量删除角色的权限
     */
    @Modifying
    @Query("DELETE FROM RolePermissionDbo rp WHERE rp.roleId = :roleId AND rp.permissionId IN :permissionIds")
    void deleteByRoleIdAndPermissionIds(@Param("roleId") String roleId, @Param("permissionIds") List<String> permissionIds);
}

