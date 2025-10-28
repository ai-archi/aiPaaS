package com.aixone.workbench.menu.domain.repository;

import com.aixone.workbench.menu.domain.model.UserMenuCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户菜单个性化配置仓储接口
 * 负责用户个性化配置的持久化操作
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface UserMenuCustomRepository extends JpaRepository<UserMenuCustom, UUID> {
    
    /**
     * 根据用户ID查询所有个性化配置
     */
    List<UserMenuCustom> findByUserId(UUID userId);
    
    /**
     * 根据用户ID和租户ID查询个性化配置（多租户隔离）
     */
    List<UserMenuCustom> findByUserIdAndTenantId(UUID userId, UUID tenantId);
    
    /**
     * 根据用户ID、租户ID和菜单ID查询个性化配置
     */
    Optional<UserMenuCustom> findByUserIdAndTenantIdAndMenuId(UUID userId, UUID tenantId, UUID menuId);
    
    /**
     * 根据用户ID查询快捷入口配置
     */
    List<UserMenuCustom> findByUserIdAndIsQuickEntry(UUID userId, Boolean isQuickEntry);
    
    /**
     * 检查用户是否隐藏了某个菜单
     */
    boolean existsByUserIdAndTenantIdAndMenuIdAndIsHidden(UUID userId, UUID tenantId, UUID menuId, Boolean isHidden);
    
    /**
     * 删除用户的所有菜单个性化配置
     */
    void deleteByUserIdAndTenantId(UUID userId, UUID tenantId);
    
    /**
     * 删除指定菜单的个性化配置
     */
    void deleteByUserIdAndTenantIdAndMenuId(UUID userId, UUID tenantId, UUID menuId);
}
