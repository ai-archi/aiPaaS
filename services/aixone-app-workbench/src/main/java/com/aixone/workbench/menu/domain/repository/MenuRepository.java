package com.aixone.workbench.menu.domain.repository;

import com.aixone.workbench.menu.domain.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 菜单仓储接口
 * 负责菜单的持久化操作
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    
    /**
     * 根据租户ID查询所有菜单，按显示顺序排序
     */
    List<Menu> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
    
    /**
     * 根据父菜单ID查询子菜单
     */
    List<Menu> findByParentIdOrderByDisplayOrderAsc(UUID parentId);
    
    /**
     * 根据租户ID和父菜单ID查询子菜单
     */
    List<Menu> findByTenantIdAndParentIdOrderByDisplayOrderAsc(UUID tenantId, UUID parentId);
    
    /**
     * 根据租户ID和菜单类型查询菜单
     */
    List<Menu> findByTenantIdAndTypeOrderByDisplayOrderAsc(UUID tenantId, String type);
    
    /**
     * 根据ID和租户ID查询菜单（用于多租户隔离）
     */
    Optional<Menu> findByIdAndTenantId(UUID id, UUID tenantId);
    
    /**
     * 检查菜单是否存在
     */
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
    
    /**
     * 根据菜单ID列表和租户ID查询菜单
     */
    List<Menu> findByIdInAndTenantId(List<UUID> menuIds, UUID tenantId);
}
