package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限 JPA 仓储
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionDbo, String>, JpaSpecificationExecutor<PermissionDbo> {
    
    /**
     * 根据租户ID查找所有权限
     */
    @Query("SELECT p FROM PermissionDbo p WHERE p.tenantId = :tenantId ORDER BY p.createdAt ASC")
    List<PermissionDbo> findByTenantId(@Param("tenantId") String tenantId);
    
    /**
     * 根据租户ID分页查找权限
     */
    @Query("SELECT p FROM PermissionDbo p WHERE p.tenantId = :tenantId ORDER BY p.createdAt ASC")
    Page<PermissionDbo> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);
    
    /**
     * 根据租户ID和编码查找权限
     */
    @Query("SELECT p FROM PermissionDbo p WHERE p.tenantId = :tenantId AND p.code = :code")
    Optional<PermissionDbo> findByTenantIdAndCode(@Param("tenantId") String tenantId, @Param("code") String code);
    
    /**
     * 根据租户ID、资源标识和操作标识查找权限
     */
    @Query("SELECT p FROM PermissionDbo p WHERE p.tenantId = :tenantId AND p.resource = :resource AND p.action = :action")
    Optional<PermissionDbo> findByTenantIdAndResourceAndAction(
            @Param("tenantId") String tenantId, 
            @Param("resource") String resource, 
            @Param("action") String action);
    
    /**
     * 检查编码是否存在
     */
    boolean existsByTenantIdAndCode(String tenantId, String code);
}

