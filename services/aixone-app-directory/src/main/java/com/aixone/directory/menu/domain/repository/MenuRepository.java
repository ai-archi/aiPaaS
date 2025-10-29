package com.aixone.directory.menu.domain.repository;

import com.aixone.directory.menu.domain.aggregate.Menu;

import java.util.List;
import java.util.Optional;

/**
 * 菜单仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface MenuRepository {
    
    /**
     * 保存菜单
     */
    Menu save(Menu menu);
    
    /**
     * 根据ID查找菜单
     */
    Optional<Menu> findById(String menuId);
    
    /**
     * 根据租户ID查找所有菜单
     */
    List<Menu> findByTenantId(String tenantId);
    
    /**
     * 根据租户ID和父菜单ID查找子菜单
     */
    List<Menu> findByTenantIdAndParentId(String tenantId, String parentId);
    
    /**
     * 根据租户ID查找根菜单（parentId为null）
     */
    List<Menu> findRootMenusByTenantId(String tenantId);
    
    /**
     * 删除菜单
     */
    void delete(String menuId);
    
    /**
     * 检查菜单名称是否存在
     */
    boolean existsByNameAndTenantId(String name, String tenantId);
}
