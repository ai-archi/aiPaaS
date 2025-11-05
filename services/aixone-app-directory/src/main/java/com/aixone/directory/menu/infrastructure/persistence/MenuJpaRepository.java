package com.aixone.directory.menu.infrastructure.persistence;

import com.aixone.directory.menu.infrastructure.persistence.dbo.MenuDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单 JPA 仓储
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface MenuJpaRepository extends JpaRepository<MenuDbo, String>, JpaSpecificationExecutor<MenuDbo> {
    
    /**
     * 根据租户ID查找所有菜单，按显示顺序排序
     */
    @Query("SELECT m FROM MenuDbo m WHERE m.tenantId = :tenantId ORDER BY m.displayOrder ASC, m.createdAt ASC")
    List<MenuDbo> findByTenantIdOrderByDisplayOrderAsc(@Param("tenantId") String tenantId);
    
    /**
     * 根据租户ID分页查找菜单，按显示顺序排序
     */
    @Query("SELECT m FROM MenuDbo m WHERE m.tenantId = :tenantId ORDER BY m.displayOrder ASC, m.createdAt ASC")
    Page<MenuDbo> findByTenantIdOrderByDisplayOrderAsc(@Param("tenantId") String tenantId, Pageable pageable);
    
    /**
     * 根据租户ID和父菜单ID查找子菜单
     */
    @Query("SELECT m FROM MenuDbo m WHERE m.tenantId = :tenantId AND m.parentId = :parentId ORDER BY m.displayOrder ASC")
    List<MenuDbo> findByTenantIdAndParentId(@Param("tenantId") String tenantId, @Param("parentId") String parentId);
    
    /**
     * 根据租户ID查找根菜单（parentId为null）
     */
    @Query("SELECT m FROM MenuDbo m WHERE m.tenantId = :tenantId AND m.parentId IS NULL ORDER BY m.displayOrder ASC")
    List<MenuDbo> findRootMenusByTenantId(@Param("tenantId") String tenantId);
    
    /**
     * 检查菜单名称是否存在
     */
    boolean existsByNameAndTenantId(String name, String tenantId);
}
