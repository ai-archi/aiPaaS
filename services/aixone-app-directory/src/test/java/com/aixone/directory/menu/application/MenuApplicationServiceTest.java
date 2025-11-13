package com.aixone.directory.menu.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.menu.domain.aggregate.Menu;
import com.aixone.directory.menu.domain.repository.MenuRepository;
import com.aixone.directory.menu.infrastructure.persistence.MenuJpaRepository;
import com.aixone.directory.menu.infrastructure.persistence.dbo.MenuDbo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 菜单应用服务单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单应用服务测试")
class MenuApplicationServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuJpaRepository menuJpaRepository;

    @InjectMocks
    private MenuApplicationService menuApplicationService;

    private String tenantId;
    private String menuId;
    private Menu testMenu;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        menuId = "menu-" + UUID.randomUUID().toString();
        
        testMenu = Menu.builder()
                .id(menuId)
                .tenantId(tenantId)
                .name("测试菜单")
                .title("测试菜单标题")
                .path("/test")
                .type("menu")
                .renderType("tab")
                .keepalive(false)
                .displayOrder(0)
                .visible(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("创建菜单 - 成功")
    void testCreateMenu_Success() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .icon("icon-menu")
                .type("menu")
                .renderType("tab")
                .component("views/menu/index")
                .keepalive(true)
                .displayOrder(1)
                .visible(true)
                .build();

        when(menuRepository.existsByNameAndTenantId(command.getName(), tenantId)).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            menu.setId(menuId);
            return menu;
        });

        // When
        MenuDto.MenuView result = menuApplicationService.createMenu(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("新菜单", result.getName());
        assertEquals("新菜单标题", result.getTitle());
        assertEquals("/new-menu", result.getPath());
        assertEquals(tenantId, result.getTenantId());
        assertEquals("icon-menu", result.getIcon());
        assertEquals("menu", result.getType());
        assertEquals("tab", result.getRenderType());
        assertEquals("views/menu/index", result.getComponent());
        assertTrue(result.getKeepalive());
        assertEquals(1, result.getDisplayOrder());
        assertTrue(result.getVisible());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(menuRepository, times(1)).existsByNameAndTenantId(command.getName(), tenantId);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 名称为空")
    void testCreateMenu_EmptyName_ThrowsException() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("")
                .title("菜单标题")
                .path("/menu")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.createMenu(command);
        });

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 标题为空")
    void testCreateMenu_EmptyTitle_ThrowsException() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("菜单名称")
                .title("")
                .path("/menu")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.createMenu(command);
        });

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 路径为空")
    void testCreateMenu_EmptyPath_ThrowsException() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("菜单名称")
                .title("菜单标题")
                .path("")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.createMenu(command);
        });

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 租户ID为空")
    void testCreateMenu_EmptyTenantId_ThrowsException() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId("")
                .name("菜单名称")
                .title("菜单标题")
                .path("/menu")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.createMenu(command);
        });

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 菜单名称已存在")
    void testCreateMenu_DuplicateName_ThrowsException() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("已存在的菜单")
                .title("菜单标题")
                .path("/menu")
                .build();

        when(menuRepository.existsByNameAndTenantId(command.getName(), tenantId)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.createMenu(command);
        });

        assertEquals("菜单名称已存在", exception.getMessage());
        verify(menuRepository, times(1)).existsByNameAndTenantId(command.getName(), tenantId);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("创建菜单 - 带父菜单")
    void testCreateMenu_WithParentId() {
        // Given
        String parentId = "parent-" + UUID.randomUUID().toString();
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .parentId(parentId)
                .name("子菜单")
                .title("子菜单标题")
                .path("/child-menu")
                .build();

        when(menuRepository.existsByNameAndTenantId(command.getName(), tenantId)).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            menu.setId(menuId);
            return menu;
        });

        // When
        MenuDto.MenuView result = menuApplicationService.createMenu(command);

        // Then
        assertNotNull(result);
        assertEquals(parentId, result.getParentId());
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("根据ID查找菜单 - 成功（带租户验证）")
    void testFindMenuById_WithTenantId_Success() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When
        Optional<MenuDto.MenuView> result = menuApplicationService.findMenuById(menuId, tenantId);

        // Then
        assertTrue(result.isPresent());
        MenuDto.MenuView view = result.get();
        assertEquals(menuId, view.getId());
        assertEquals(tenantId, view.getTenantId());
        assertEquals("测试菜单", view.getName());
        assertEquals("测试菜单标题", view.getTitle());
        assertEquals("/test", view.getPath());

        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("根据ID查找菜单 - 菜单不存在")
    void testFindMenuById_NotFound() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // When
        Optional<MenuDto.MenuView> result = menuApplicationService.findMenuById(menuId, tenantId);

        // Then
        assertTrue(result.isEmpty());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("根据ID查找菜单 - 不属于当前租户")
    void testFindMenuById_DifferentTenant_ReturnsEmpty() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When
        Optional<MenuDto.MenuView> result = menuApplicationService.findMenuById(menuId, otherTenantId);

        // Then
        assertTrue(result.isEmpty());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("根据ID查找菜单 - 不带租户验证（管理接口）")
    void testFindMenuById_WithoutTenantId_Success() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When
        Optional<MenuDto.MenuView> result = menuApplicationService.findMenuById(menuId);

        // Then
        assertTrue(result.isPresent());
        MenuDto.MenuView view = result.get();
        assertEquals(menuId, view.getId());
        assertEquals(tenantId, view.getTenantId());
        verify(menuRepository, times(1)).findById(menuId);
    }

    @Test
    @DisplayName("查找菜单的子菜单 - 成功")
    void testFindMenuChildren_Success() {
        // Given
        String childMenuId1 = "child-1-" + UUID.randomUUID().toString();
        String childMenuId2 = "child-2-" + UUID.randomUUID().toString();
        
        Menu childMenu1 = Menu.builder()
                .id(childMenuId1)
                .tenantId(tenantId)
                .parentId(menuId)
                .name("子菜单1")
                .title("子菜单1标题")
                .path("/child1")
                .type("menu")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Menu childMenu2 = Menu.builder()
                .id(childMenuId2)
                .tenantId(tenantId)
                .parentId(menuId)
                .name("子菜单2")
                .title("子菜单2标题")
                .path("/child2")
                .type("menu")
                .displayOrder(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.findByTenantIdAndParentId(tenantId, menuId))
                .thenReturn(List.of(childMenu1, childMenu2));

        // When
        List<MenuDto.MenuView> result = menuApplicationService.findMenuChildren(menuId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("子菜单1", result.get(0).getName());
        assertEquals("子菜单2", result.get(1).getName());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).findByTenantIdAndParentId(tenantId, menuId);
    }

    @Test
    @DisplayName("查找菜单的子菜单 - 菜单不存在")
    void testFindMenuChildren_MenuNotFound_ThrowsException() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.findMenuChildren(menuId, tenantId);
        });

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).findByTenantIdAndParentId(anyString(), anyString());
    }

    @Test
    @DisplayName("查找菜单的子菜单 - 不属于当前租户")
    void testFindMenuChildren_DifferentTenant_ThrowsException() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.findMenuChildren(menuId, otherTenantId);
        });

        assertEquals("菜单不属于当前租户", exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).findByTenantIdAndParentId(anyString(), anyString());
    }

    @Test
    @DisplayName("分页查询菜单 - 成功")
    void testFindMenus_Success() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20, "displayOrder", "asc");
        
        MenuDbo dbo1 = createMenuDbo("menu-1", tenantId, null, "菜单1", 1);
        MenuDbo dbo2 = createMenuDbo("menu-2", tenantId, null, "菜单2", 2);
        List<MenuDbo> content = List.of(dbo1, dbo2);
        
        Page<MenuDbo> page = new PageImpl<>(content, org.springframework.data.domain.PageRequest.of(0, 20), 2);

        when(menuJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<MenuDto.MenuView> result = menuApplicationService.findMenus(
                pageRequest, tenantId, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getList().size());
        assertEquals("菜单1", result.getList().get(0).getName());
        assertEquals("菜单2", result.getList().get(1).getName());
        verify(menuJpaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("分页查询菜单 - 查询根菜单（parentId=null）")
    void testFindMenus_WithNullParentId_ReturnsRootMenus() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        
        MenuDbo rootMenu = createMenuDbo("root-menu", tenantId, null, "根菜单", 1);
        Page<MenuDbo> page = new PageImpl<>(List.of(rootMenu), org.springframework.data.domain.PageRequest.of(0, 20), 1);

        when(menuJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<MenuDto.MenuView> result = menuApplicationService.findMenus(
                pageRequest, tenantId, "null", null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertNull(result.getList().get(0).getParentId());
        verify(menuJpaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("分页查询菜单 - 按名称过滤")
    void testFindMenus_WithNameFilter() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        
        MenuDbo menu = createMenuDbo("menu-1", tenantId, null, "测试菜单", 1);
        Page<MenuDbo> page = new PageImpl<>(List.of(menu), org.springframework.data.domain.PageRequest.of(0, 20), 1);

        when(menuJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        PageResult<MenuDto.MenuView> result = menuApplicationService.findMenus(
                pageRequest, tenantId, null, "测试", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertTrue(result.getList().get(0).getName().contains("测试"));
        verify(menuJpaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("更新菜单 - 成功（带租户验证）")
    void testUpdateMenu_WithTenantId_Success() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .icon("updated-icon")
                .displayOrder(10)
                .visible(false)
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MenuDto.MenuView result = menuApplicationService.updateMenu(menuId, tenantId, command);

        // Then
        assertNotNull(result);
        assertEquals("更新后的菜单", result.getName());
        assertEquals("更新后的标题", result.getTitle());
        assertEquals("/updated", result.getPath());
        assertEquals("updated-icon", result.getIcon());
        assertEquals(10, result.getDisplayOrder());
        assertFalse(result.getVisible());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("更新菜单 - 菜单不存在")
    void testUpdateMenu_MenuNotFound_ThrowsException() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.updateMenu(menuId, tenantId, command);
        });

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("更新菜单 - 不属于当前租户")
    void testUpdateMenu_DifferentTenant_ThrowsException() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.updateMenu(menuId, otherTenantId, command);
        });

        assertEquals("菜单不属于当前租户", exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("更新菜单 - 不带租户验证（管理接口）")
    void testUpdateMenu_WithoutTenantId_Success() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MenuDto.MenuView result = menuApplicationService.updateMenu(menuId, command);

        // Then
        assertNotNull(result);
        assertEquals("更新后的菜单", result.getName());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("删除菜单 - 成功（带租户验证）")
    void testDeleteMenu_WithTenantId_Success() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.findByTenantIdAndParentId(tenantId, menuId)).thenReturn(new ArrayList<>());
        doNothing().when(menuRepository).delete(menuId);

        // When
        menuApplicationService.deleteMenu(menuId, tenantId);

        // Then
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).findByTenantIdAndParentId(tenantId, menuId);
        verify(menuRepository, times(1)).delete(menuId);
    }

    @Test
    @DisplayName("删除菜单 - 菜单不存在")
    void testDeleteMenu_MenuNotFound_ThrowsException() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.deleteMenu(menuId, tenantId);
        });

        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("删除菜单 - 不属于当前租户")
    void testDeleteMenu_DifferentTenant_ThrowsException() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.deleteMenu(menuId, otherTenantId);
        });

        assertEquals("菜单不属于当前租户", exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("删除菜单 - 存在子菜单")
    void testDeleteMenu_WithChildren_ThrowsException() {
        // Given
        Menu childMenu = Menu.builder()
                .id("child-menu")
                .tenantId(tenantId)
                .parentId(menuId)
                .name("子菜单")
                .title("子菜单标题")
                .path("/child")
                .type("menu")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.findByTenantIdAndParentId(tenantId, menuId)).thenReturn(List.of(childMenu));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuApplicationService.deleteMenu(menuId, tenantId);
        });

        assertEquals("菜单存在子菜单，无法删除", exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).findByTenantIdAndParentId(tenantId, menuId);
        verify(menuRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("删除菜单 - 不带租户验证（管理接口）")
    void testDeleteMenu_WithoutTenantId_Success() {
        // Given
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(testMenu));
        when(menuRepository.findByTenantIdAndParentId(tenantId, menuId)).thenReturn(new ArrayList<>());
        doNothing().when(menuRepository).delete(menuId);

        // When
        menuApplicationService.deleteMenu(menuId);

        // Then
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).findByTenantIdAndParentId(tenantId, menuId);
        verify(menuRepository, times(1)).delete(menuId);
    }

    @Test
    @DisplayName("根据租户ID查找所有菜单 - 成功")
    void testFindMenusByTenantId_Success() {
        // Given
        Menu menu1 = Menu.builder()
                .id("menu-1")
                .tenantId(tenantId)
                .parentId(null)
                .name("菜单1")
                .title("菜单1标题")
                .path("/menu1")
                .type("menu")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Menu menu2 = Menu.builder()
                .id("menu-2")
                .tenantId(tenantId)
                .parentId("menu-1")
                .name("菜单2")
                .title("菜单2标题")
                .path("/menu2")
                .type("menu")
                .displayOrder(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuRepository.findByTenantId(tenantId)).thenReturn(List.of(menu1, menu2));

        // When
        List<MenuDto.MenuView> result = menuApplicationService.findMenusByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // 只返回根菜单（树形结构）
        assertEquals("菜单1", result.get(0).getName());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("菜单2", result.get(0).getChildren().get(0).getName());
        verify(menuRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("根据租户ID查找根菜单 - 成功")
    void testFindRootMenusByTenantId_Success() {
        // Given
        Menu rootMenu = Menu.builder()
                .id("root-menu")
                .tenantId(tenantId)
                .parentId(null)
                .name("根菜单")
                .title("根菜单标题")
                .path("/root")
                .type("menu")
                .displayOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuRepository.findRootMenusByTenantId(tenantId)).thenReturn(List.of(rootMenu));

        // When
        List<MenuDto.MenuView> result = menuApplicationService.findRootMenusByTenantId(tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("根菜单", result.get(0).getName());
        assertNull(result.get(0).getParentId());
        verify(menuRepository, times(1)).findRootMenusByTenantId(tenantId);
    }

    /**
     * 创建测试用的MenuDbo对象
     */
    private MenuDbo createMenuDbo(String id, String tenantId, String parentId, String name, Integer displayOrder) {
        return MenuDbo.builder()
                .id(id)
                .tenantId(tenantId)
                .parentId(parentId)
                .name(name)
                .title(name + "标题")
                .path("/" + name.toLowerCase())
                .type("menu")
                .renderType("tab")
                .keepalive(false)
                .displayOrder(displayOrder)
                .visible(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

