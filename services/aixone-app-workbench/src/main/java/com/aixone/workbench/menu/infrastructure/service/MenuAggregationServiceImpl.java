package com.aixone.workbench.menu.infrastructure.service;

import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.application.dto.UserMenuCustomDTO;
import com.aixone.workbench.menu.domain.model.Menu;
import com.aixone.workbench.menu.domain.model.UserMenuCustom;
import com.aixone.workbench.menu.domain.remote.DirectoryServiceClient;
import com.aixone.workbench.menu.domain.repository.MenuRepository;
import com.aixone.workbench.menu.domain.repository.UserMenuCustomRepository;
import com.aixone.workbench.menu.domain.service.MenuAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单聚合服务实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MenuAggregationServiceImpl implements MenuAggregationService {
    
    private final MenuRepository menuRepository;
    private final UserMenuCustomRepository userMenuCustomRepository;
    private final DirectoryServiceClient directoryServiceClient;
    
    @Override
    @Cacheable(value = "menuCache", key = "'menu:user:' + #userId + ':tenant:' + #tenantId")
    public List<MenuDTO> aggregateVisibleMenus(UUID userId, UUID tenantId, List<UUID> userRoles) {
        log.info("聚合用户可见菜单: userId={}, tenantId={}, roles={}", userId, tenantId, userRoles);
        
        try {
            // 1. 从数据库获取菜单数据
            List<Menu> menus = menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
            
            // 2. 获取用户个性化配置
            Map<String, UserMenuCustomDTO> customConfigs = getUserMenuCustomConfig(userId, tenantId);
            
            // 3. 构建菜单树
            List<MenuDTO> menuTree = buildMenuTree(menus, customConfigs);
            
            log.info("聚合菜单完成，共{}个根菜单", menuTree.size());
            return menuTree;
            
        } catch (Exception e) {
            log.error("聚合菜单失败: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Map<String, UserMenuCustomDTO> getUserMenuCustomConfig(UUID userId, UUID tenantId) {
        List<UserMenuCustom> customs = userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId);
        return customs.stream()
                .collect(Collectors.toMap(
                    custom -> custom.getMenuId() != null ? custom.getMenuId().toString() : "unknown",
                    this::toUserMenuCustomDTO,
                    (existing, replacement) -> replacement
                ));
    }
    
    @Override
    @Transactional
    public void saveUserMenuCustom(UUID userId, UUID tenantId, UUID menuId, String config) {
        log.info("保存用户菜单个性化配置: userId={}, menuId={}", userId, menuId);
        
        Optional<UserMenuCustom> existing = userMenuCustomRepository
                .findByUserIdAndTenantIdAndMenuId(userId, tenantId, menuId);
        
        if (existing.isPresent()) {
            UserMenuCustom custom = existing.get();
            custom.setConfig(config);
            userMenuCustomRepository.save(custom);
        } else {
            UserMenuCustom custom = UserMenuCustom.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .menuId(menuId)
                    .config(config)
                    .build();
            userMenuCustomRepository.save(custom);
        }
    }
    
    @Override
    @Cacheable(value = "menuCache", key = "'menu:tenant:' + #tenantId")
    public List<MenuDTO> buildMenuTree(UUID tenantId, List<UUID> userRoles) {
        List<Menu> menus = menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
        return buildMenuTree(menus, java.util.Collections.emptyMap());
    }
    
    /**
     * 构建菜单树
     */
    private List<MenuDTO> buildMenuTree(List<Menu> menus, Map<String, UserMenuCustomDTO> customConfigs) {
        // 过滤可见菜单
        List<Menu> visibleMenus = menus.stream()
                .filter(Menu::isVisible)
                .collect(Collectors.toList());
        
        // 转换为DTO
        List<MenuDTO> menuDTOs = visibleMenus.stream()
                .map(this::toMenuDTO)
                .collect(Collectors.toList());
        
        // 应用个性化配置
        menuDTOs.forEach(dto -> applyCustomConfig(dto, customConfigs.get(dto.getId())));
        
        // 再次过滤隐藏的菜单
        menuDTOs = menuDTOs.stream()
                .filter(menu -> menu.getVisible() == null || menu.getVisible())
                .collect(Collectors.toList());
        
        // 构建树形结构
        Map<String, MenuDTO> menuMap = menuDTOs.stream()
                .collect(Collectors.toMap(MenuDTO::getId, dto -> dto));
        
        List<MenuDTO> rootMenus = new ArrayList<>();
        for (MenuDTO menu : menuDTOs) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            } else {
                MenuDTO parent = menuMap.get(menu.getParentId() != null ? menu.getParentId().toString() : null);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }
        
        // 排序
        sortMenus(rootMenus);
        
        return rootMenus;
    }
    
    /**
     * 应用个性化配置
     */
    private void applyCustomConfig(MenuDTO menu, UserMenuCustomDTO custom) {
        if (custom != null) {
            if (custom.getIsHidden() != null && custom.getIsHidden()) {
                menu.setVisible(false);
            }
            if (custom.getIsQuickEntry() != null && custom.getIsQuickEntry()) {
                menu.setVisible(true);
            }
            if (custom.getCustomOrder() != null) {
                menu.setDisplayOrder(custom.getCustomOrder());
            }
        }
    }
    
    /**
     * 递归排序菜单
     */
    private void sortMenus(List<MenuDTO> menus) {
        menus.sort(Comparator.comparing(menu -> 
            menu.getDisplayOrder() != null ? menu.getDisplayOrder() : Integer.MAX_VALUE));
        menus.forEach(menu -> {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortMenus(menu.getChildren());
            }
        });
    }
    
    /**
     * 转换Menu为MenuDTO
     * 匹配前端期望的菜单结构
     */
    private MenuDTO toMenuDTO(Menu menu) {
        return MenuDTO.builder()
                .id(menu.getId() != null ? menu.getId().toString() : null)
                .tenantId(menu.getTenantId())
                .parentId(menu.getParentId())
                .name(menu.getName())
                .title(menu.getTitle())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .type(menu.getType())  // 菜单类型：menu、menu_dir、button
                .renderType(menu.getRenderType() != null ? menu.getRenderType() : "tab") // 渲染类型：tab、iframe、link，默认tab
                .component(menu.getComponent())
                .url(menu.getUrl())
                .keepalive(menu.getKeepalive())
                .displayOrder(menu.getDisplayOrder())
                .visible(menu.isVisible())
                .config(menu.getConfig())
                .extend(menu.getExtend())
                .children(new ArrayList<>())
                .build();
    }
    
    /**
     * 转换UserMenuCustom为UserMenuCustomDTO
     */
    private UserMenuCustomDTO toUserMenuCustomDTO(UserMenuCustom custom) {
        return UserMenuCustomDTO.builder()
                .id(custom.getId())
                .userId(custom.getUserId())
                .tenantId(custom.getTenantId())
                .menuId(custom.getMenuId())
                .config(custom.getConfig())
                .isQuickEntry(custom.isQuickEntry())
                .customOrder(custom.getCustomOrder())
                .isHidden(custom.isHidden())
                .build();
    }
    
    /**
     * 将目录服务的菜单数据转换为领域模型
     */
    private List<Menu> mapDirectoryMenusToDomain(List<Map<String, Object>> directoryMenus, UUID tenantId) {
        return directoryMenus.stream()
                .map(map -> {
                    // 确保 type 是小写格式
                    String typeValue = map.getOrDefault("type", "menu").toString().toLowerCase();
                    return Menu.builder()
                            .id(UUID.fromString(map.get("id").toString()))
                            .tenantId(tenantId)
                            .parentId(map.get("parentId") != null ? UUID.fromString(map.get("parentId").toString()) : null)
                            .name((String) map.get("name"))
                            .title((String) map.get("title"))
                            .path((String) map.get("path"))
                            .icon((String) map.get("icon"))
                            .type(typeValue)  // 直接使用字符串
                            .renderType((String) map.get("renderType"))
                            .component((String) map.get("component"))
                            .url((String) map.get("url"))
                            .keepalive(map.get("keepalive") != null ? (Boolean) map.get("keepalive") : false)
                            .displayOrder(map.get("displayOrder") != null ? ((Number) map.get("displayOrder")).intValue() : 0)
                            .visible(map.get("visible") != null ? (Boolean) map.get("visible") : true)
                            .config((String) map.get("config"))
                            .extend((String) map.get("extend"))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
