package com.aixone.directory.menu.infrastructure.persistence;

import com.aixone.directory.menu.domain.aggregate.Menu;
import com.aixone.directory.menu.domain.repository.MenuRepository;
import com.aixone.directory.menu.infrastructure.persistence.dbo.MenuDbo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 菜单仓储实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class PostgresMenuRepository implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;

    @Override
    public Menu save(Menu menu) {
        MenuDbo dbo = convertToDbo(menu);
        MenuDbo savedDbo = menuJpaRepository.save(dbo);
        return convertToDomain(savedDbo);
    }

    @Override
    public Optional<Menu> findById(String menuId) {
        return menuJpaRepository.findById(menuId)
                .map(this::convertToDomain);
    }

    @Override
    public List<Menu> findByTenantId(String tenantId) {
        List<MenuDbo> dbos = menuJpaRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
        return dbos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findByTenantIdAndParentId(String tenantId, String parentId) {
        List<MenuDbo> dbos = menuJpaRepository.findByTenantIdAndParentId(tenantId, parentId);
        return dbos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findRootMenusByTenantId(String tenantId) {
        List<MenuDbo> dbos = menuJpaRepository.findRootMenusByTenantId(tenantId);
        return dbos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String menuId) {
        menuJpaRepository.deleteById(menuId);
    }

    @Override
    public boolean existsByNameAndTenantId(String name, String tenantId) {
        return menuJpaRepository.existsByNameAndTenantId(name, tenantId);
    }

    /**
     * 转换为数据库对象
     */
    private MenuDbo convertToDbo(Menu menu) {
        return MenuDbo.builder()
                .id(menu.getId())
                .tenantId(menu.getTenantId())
                .parentId(menu.getParentId())
                .name(menu.getName())
                .title(menu.getTitle())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .type(menu.getType())
                .renderType(menu.getRenderType())
                .component(menu.getComponent())
                .url(menu.getUrl())
                .keepalive(menu.getKeepalive())
                .displayOrder(menu.getDisplayOrder())
                .visible(menu.getVisible())
                .config(menu.getConfig())
                .extend(menu.getExtend())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    /**
     * 转换为领域对象
     */
    private Menu convertToDomain(MenuDbo dbo) {
        return Menu.builder()
                .id(dbo.getId())
                .tenantId(dbo.getTenantId())
                .parentId(dbo.getParentId())
                .name(dbo.getName())
                .title(dbo.getTitle())
                .path(dbo.getPath())
                .icon(dbo.getIcon())
                .type(dbo.getType())
                .renderType(dbo.getRenderType())
                .component(dbo.getComponent())
                .url(dbo.getUrl())
                .keepalive(dbo.getKeepalive())
                .displayOrder(dbo.getDisplayOrder())
                .visible(dbo.getVisible())
                .config(dbo.getConfig())
                .extend(dbo.getExtend())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }
}
