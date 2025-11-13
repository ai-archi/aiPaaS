package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionRuleDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限规则 JPA 仓储
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface PermissionRuleJpaRepository extends JpaRepository<PermissionRuleDbo, String>, JpaSpecificationExecutor<PermissionRuleDbo> {
    
    /**
     * 根据租户ID查找所有权限规则，按优先级降序排序
     */
    @Query("SELECT pr FROM PermissionRuleDbo pr WHERE pr.tenantId = :tenantId AND pr.enabled = true ORDER BY pr.priority DESC, pr.createdAt ASC")
    List<PermissionRuleDbo> findByTenantIdAndEnabledOrderByPriorityDesc(@Param("tenantId") String tenantId);
    
    /**
     * 根据租户ID分页查找权限规则，按优先级降序排序
     */
    @Query("SELECT pr FROM PermissionRuleDbo pr WHERE pr.tenantId = :tenantId ORDER BY pr.priority DESC, pr.createdAt ASC")
    Page<PermissionRuleDbo> findByTenantIdOrderByPriorityDesc(@Param("tenantId") String tenantId, Pageable pageable);
    
    /**
     * 根据租户ID查找所有权限规则（包括禁用的）
     */
    @Query("SELECT pr FROM PermissionRuleDbo pr WHERE pr.tenantId = :tenantId ORDER BY pr.priority DESC, pr.createdAt ASC")
    List<PermissionRuleDbo> findByTenantId(@Param("tenantId") String tenantId);
}

