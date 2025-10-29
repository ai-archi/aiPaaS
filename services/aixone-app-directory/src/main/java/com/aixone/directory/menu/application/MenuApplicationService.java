package com.aixone.directory.menu.application;

import com.aixone.directory.menu.domain.aggregate.Menu;
import com.aixone.directory.menu.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 菜单应用服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private final MenuRepository menuRepository;

    /**
     * 创建菜单
     */
    @Transactional
    public MenuDto.MenuView createMenu(MenuDto.CreateMenuCommand command) {
        log.info("创建菜单: name={}, tenantId={}", command.getName(), command.getTenantId());

        Assert.hasText(command.getName(), "菜单名称不能为空");
        Assert.hasText(command.getTitle(), "菜单标题不能为空");
        Assert.hasText(command.getPath(), "菜单路径不能为空");
        Assert.hasText(command.getTenantId(), "租户ID不能为空");

        // 检查菜单名称是否已存在
        if (menuRepository.existsByNameAndTenantId(command.getName(), command.getTenantId())) {
            throw new IllegalArgumentException("菜单名称已存在");
        }

        Menu menu = Menu.create(command.getTenantId(), command.getName(), command.getTitle(), command.getPath());
        
        // 设置其他属性
        if (StringUtils.hasText(command.getParentId())) {
            menu.setParent(command.getParentId());
        }
        if (command.getIcon() != null) {
            menu.setIcon(command.getIcon());
        }
        if (command.getType() != null) {
            menu.setType(command.getType());
        }
        if (command.getRenderType() != null) {
            menu.setRenderType(command.getRenderType());
        }
        if (command.getComponent() != null) {
            menu.setComponent(command.getComponent());
        }
        if (command.getUrl() != null) {
            menu.setUrl(command.getUrl());
        }
        if (command.getKeepalive() != null) {
            menu.setKeepalive(command.getKeepalive());
        }
        if (command.getDisplayOrder() != null) {
            menu.setDisplayOrder(command.getDisplayOrder());
        }
        if (command.getVisible() != null) {
            menu.setVisible(command.getVisible());
        }
        if (command.getConfig() != null) {
            menu.setConfig(command.getConfig());
        }
        if (command.getExtend() != null) {
            menu.setExtend(command.getExtend());
        }

        Menu savedMenu = menuRepository.save(menu);
        return convertToView(savedMenu);
    }

    /**
     * 根据ID查找菜单
     */
    public Optional<MenuDto.MenuView> findMenuById(String menuId) {
        return menuRepository.findById(menuId)
                .map(this::convertToView);
    }

    /**
     * 根据租户ID查找所有菜单（树形结构）
     */
    public List<MenuDto.MenuView> findMenusByTenantId(String tenantId) {
        List<Menu> menus = menuRepository.findByTenantId(tenantId);
        return buildMenuTree(menus);
    }

    /**
     * 根据租户ID查找根菜单
     */
    public List<MenuDto.MenuView> findRootMenusByTenantId(String tenantId) {
        List<Menu> rootMenus = menuRepository.findRootMenusByTenantId(tenantId);
        return rootMenus.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    /**
     * 更新菜单
     */
    @Transactional
    public MenuDto.MenuView updateMenu(String menuId, MenuDto.UpdateMenuCommand command) {
        log.info("更新菜单: id={}, name={}", menuId, command.getName());

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));

        menu.update(command.getName(), command.getTitle(), command.getPath());
        
        // 更新其他属性
        if (command.getIcon() != null) {
            menu.setIcon(command.getIcon());
        }
        if (command.getType() != null) {
            menu.setType(command.getType());
        }
        if (command.getRenderType() != null) {
            menu.setRenderType(command.getRenderType());
        }
        if (command.getComponent() != null) {
            menu.setComponent(command.getComponent());
        }
        if (command.getUrl() != null) {
            menu.setUrl(command.getUrl());
        }
        if (command.getKeepalive() != null) {
            menu.setKeepalive(command.getKeepalive());
        }
        if (command.getDisplayOrder() != null) {
            menu.setDisplayOrder(command.getDisplayOrder());
        }
        if (command.getVisible() != null) {
            menu.setVisible(command.getVisible());
        }
        if (command.getConfig() != null) {
            menu.setConfig(command.getConfig());
        }
        if (command.getExtend() != null) {
            menu.setExtend(command.getExtend());
        }

        Menu savedMenu = menuRepository.save(menu);
        return convertToView(savedMenu);
    }

    /**
     * 删除菜单
     */
    @Transactional
    public void deleteMenu(String menuId) {
        log.info("删除菜单: id={}", menuId);

        if (!menuRepository.findById(menuId).isPresent()) {
            throw new IllegalArgumentException("菜单不存在");
        }

        menuRepository.delete(menuId);
    }

    /**
     * 构建菜单树
     */
    private List<MenuDto.MenuView> buildMenuTree(List<Menu> menus) {
        // 转换为View对象
        List<MenuDto.MenuView> menuViews = menus.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());

        // 构建父子关系
        for (MenuDto.MenuView menu : menuViews) {
            if (menu.getParentId() != null) {
                menuViews.stream()
                        .filter(parent -> parent.getId().equals(menu.getParentId()))
                        .findFirst()
                        .ifPresent(parent -> {
                            if (parent.getChildren() == null) {
                                parent.setChildren(new java.util.ArrayList<>());
                            }
                            parent.getChildren().add(menu);
                        });
            }
        }

        // 返回根菜单
        return menuViews.stream()
                .filter(menu -> menu.getParentId() == null)
                .collect(Collectors.toList());
    }

    /**
     * 转换为View对象
     */
    private MenuDto.MenuView convertToView(Menu menu) {
        return MenuDto.MenuView.builder()
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
}
