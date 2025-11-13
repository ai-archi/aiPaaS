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
     * 根据ID查找菜单（带租户验证）
     */
    public Optional<MenuDto.MenuView> findMenuById(String menuId, String tenantId) {
        return menuRepository.findById(menuId)
                .filter(menu -> menu.getTenantId().equals(tenantId))
                .map(this::convertToView);
    }

    /**
     * 根据ID查找菜单（不带租户验证，用于管理接口）
     */
    public Optional<MenuDto.MenuView> findMenuById(String menuId) {
        return menuRepository.findById(menuId)
                .map(this::convertToView);
    }

    /**
     * 查找菜单的子菜单
     */
    public List<MenuDto.MenuView> findMenuChildren(String menuId, String tenantId) {
        // 先验证菜单是否存在且属于当前租户
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));
        
        if (!menu.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("菜单不属于当前租户");
        }
        
        // 查询子菜单
        List<Menu> children = menuRepository.findByTenantIdAndParentId(tenantId, menuId);
        return children.stream()
                .map(this::convertToView)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询菜单列表（平铺结构，支持过滤）
     */
    public PageResult<MenuDto.MenuView> findMenus(PageRequest pageRequest, String tenantId, String parentId, String name, String title, String type) {
        log.info("分页查询菜单: pageNum={}, pageSize={}, tenantId={}, parentId={}, name={}, title={}, type={}", 
                pageRequest.getPageNum(), pageRequest.getPageSize(), tenantId, parentId, name, title, type);
        
        // 验证 tenantId 不能为空
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        
        // 构建查询规格
        Specification<MenuDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持parentId过滤
            // 注意：
            //   - 如果parentId参数为"null"字符串，查询根菜单（parentId为null）
            //   - 如果parentId参数为具体的菜单ID，查询该菜单的子菜单
            //   - 如果parentId参数未提供（null或空字符串），返回所有菜单（不限制parentId）
            if (StringUtils.hasText(parentId)) {
                if ("null".equalsIgnoreCase(parentId)) {
                    // 查询根菜单（parentId为null）
                    predicates.add(cb.isNull(root.get("parentId")));
                } else {
                    // 查询指定父菜单的子菜单
                    predicates.add(cb.equal(root.get("parentId"), parentId));
                }
            }
            // 如果parentId未提供，不添加parentId过滤条件，返回所有菜单
            
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
     * 根据租户ID查找所有菜单（树形结构）
     */
    public List<MenuDto.MenuView> findMenusByTenantId(String tenantId) {
        List<Menu> menus = menuRepository.findByTenantId(tenantId);
        return buildMenuTree(menus);
    }

    /**
     * 查找菜单树（支持过滤和快速搜索）
     * 返回树形结构数据，不分页
     */
    @Transactional(readOnly = true)
    public List<MenuDto.MenuView> findMenusTree(String tenantId, String name, String title, String type, String quickSearch) {
        // 构建查询规格
        Specification<MenuDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持name过滤
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            // 支持title过滤
            if (StringUtils.hasText(title)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            
            // 支持type过滤
            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            
            // 支持快速搜索（搜索title和name）
            if (StringUtils.hasText(quickSearch)) {
                String searchPattern = "%" + quickSearch.toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
                predicates.add(cb.or(titlePredicate, namePredicate));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：按displayOrder升序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("displayOrder").ascending();
        
        // 查询所有符合条件的菜单（不分页）
        List<MenuDbo> menuDbos = menuJpaRepository.findAll(spec, sort);
        
        // 转换为领域对象
        List<Menu> menus = menuDbos.stream()
                .map(this::convertDboToDomain)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildMenuTree(menus);
    }

    /**
     * 将 MenuDbo 转换为 Menu 领域对象
     */
    private Menu convertDboToDomain(MenuDbo dbo) {
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
     * 更新菜单（带租户验证）
     */
    @Transactional
    public MenuDto.MenuView updateMenu(String menuId, String tenantId, MenuDto.UpdateMenuCommand command) {
        log.info("更新菜单: id={}, tenantId={}, name={}", menuId, tenantId, command.getName());

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));
        
        // 验证菜单是否属于当前租户
        if (!menu.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("菜单不属于当前租户");
        }

        menu.update(command.getName(), command.getTitle(), command.getPath());
        
        // 更新其他属性
        // parentId 需要特殊处理：如果为 null 表示设置为根菜单，如果为空字符串也表示设置为根菜单
        if (command.getParentId() != null) {
            menu.setParent(command.getParentId());
        } else {
            // 如果 parentId 为 null，设置为根菜单（parentId = null）
            menu.setParent(null);
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
     * 更新菜单（不带租户验证，用于管理接口）
     */
    @Transactional
    public MenuDto.MenuView updateMenu(String menuId, MenuDto.UpdateMenuCommand command) {
        log.info("更新菜单: id={}, name={}", menuId, command.getName());

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));

        menu.update(command.getName(), command.getTitle(), command.getPath());
        
        // 更新其他属性
        // parentId 需要特殊处理：如果为 null 表示设置为根菜单，如果为空字符串也表示设置为根菜单
        if (command.getParentId() != null) {
            menu.setParent(command.getParentId());
        } else {
            // 如果 parentId 为 null，设置为根菜单（parentId = null）
            menu.setParent(null);
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
     * 删除菜单（带租户验证）
     */
    @Transactional
    public void deleteMenu(String menuId, String tenantId) {
        log.info("删除菜单: id={}, tenantId={}", menuId, tenantId);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));
        
        // 验证菜单是否属于当前租户
        if (!menu.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("菜单不属于当前租户");
        }
        
        // 检查是否有子菜单
        List<Menu> children = menuRepository.findByTenantIdAndParentId(tenantId, menuId);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("菜单存在子菜单，无法删除");
        }

        menuRepository.delete(menuId);
    }

    /**
     * 删除菜单（不带租户验证，用于管理接口）
     */
    @Transactional
    public void deleteMenu(String menuId) {
        log.info("删除菜单: id={}", menuId);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("菜单不存在"));
        
        // 检查是否有子菜单（需要知道租户ID）
        // 注意：管理员接口删除时，需要检查所有租户下的子菜单
        // 这里简化处理，只检查当前菜单的tenantId下的子菜单
        List<Menu> children = menuRepository.findByTenantIdAndParentId(menu.getTenantId(), menuId);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("菜单存在子菜单，无法删除");
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

        // 对每个节点的children按displayOrder排序
        sortMenuTree(menuViews);

        // 返回根菜单（按displayOrder排序）
        return menuViews.stream()
                .filter(menu -> menu.getParentId() == null)
                .sorted((a, b) -> {
                    int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : 0;
                    int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : 0;
                    return Integer.compare(orderA, orderB);
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归排序菜单树
     */
    private void sortMenuTree(List<MenuDto.MenuView> menus) {
        for (MenuDto.MenuView menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                menu.getChildren().sort((a, b) -> {
                    int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : 0;
                    int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : 0;
                    return Integer.compare(orderA, orderB);
                });
                sortMenuTree(menu.getChildren());
            }
        }
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
                .children(new java.util.ArrayList<>()) // 初始化 children 列表，避免 JSON 序列化问题
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
