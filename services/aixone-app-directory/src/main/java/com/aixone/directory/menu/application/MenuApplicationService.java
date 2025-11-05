package com.aixone.directory.menu.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.menu.domain.aggregate.Menu;
import com.aixone.directory.menu.domain.repository.MenuRepository;
import com.aixone.directory.menu.infrastructure.persistence.dbo.MenuDbo;
import com.aixone.directory.menu.infrastructure.persistence.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
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
    private final MenuJpaRepository menuJpaRepository;

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
     * 分页查询菜单列表（平铺结构，支持过滤）
     */
    public PageResult<MenuDto.MenuView> findMenus(PageRequest pageRequest, String tenantId, String name, String title, String type) {
        log.info("分页查询菜单: pageNum={}, pageSize={}, tenantId={}, name={}, title={}, type={}", 
                pageRequest.getPageNum(), pageRequest.getPageSize(), tenantId, name, title, type);
        
        // 处理特殊 tenantId 值
        String actualTenantId = convertTenantId(tenantId);
        
        // 构建查询规格
        Specification<MenuDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), actualTenantId));
            
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(title)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            
            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：优先使用请求中的排序参数，否则使用默认排序
        org.springframework.data.domain.Sort sort;
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            // 使用请求中的排序字段和方向
            org.springframework.data.domain.Sort.Direction direction = 
                "desc".equalsIgnoreCase(pageRequest.getSortDirection()) 
                    ? org.springframework.data.domain.Sort.Direction.DESC 
                    : org.springframework.data.domain.Sort.Direction.ASC;
            sort = org.springframework.data.domain.Sort.by(direction, pageRequest.getSortBy());
            // 如果排序字段不是 displayOrder，则添加 displayOrder 作为次要排序
            if (!"displayOrder".equals(pageRequest.getSortBy())) {
                sort = sort.and(org.springframework.data.domain.Sort.by("displayOrder").ascending());
            }
        } else {
            // 默认排序：按显示顺序和创建时间
            sort = org.springframework.data.domain.Sort.by("displayOrder").ascending()
                .and(org.springframework.data.domain.Sort.by("createdAt").ascending());
        }
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
            pageRequest.getPageSize(),
            sort
        );
        
        Page<MenuDbo> page = menuJpaRepository.findAll(spec, pageable);
        List<MenuDto.MenuView> content = page.getContent().stream()
                .map(this::convertDboToView)
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
    }
    
    /**
     * 转换租户ID：将 "default" 转换为默认 UUID
     */
    private String convertTenantId(String tenantId) {
        if (tenantId == null || "default".equals(tenantId)) {
            return "00000000-0000-0000-0000-000000000000";
        }
        return tenantId;
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
    
    /**
     * 将 MenuDbo 转换为 View 对象
     */
    private MenuDto.MenuView convertDboToView(MenuDbo dbo) {
        return MenuDto.MenuView.builder()
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
