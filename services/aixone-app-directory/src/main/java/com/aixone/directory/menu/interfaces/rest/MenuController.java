package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.directory.menu.application.MenuApplicationService;
import com.aixone.directory.menu.application.MenuDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 菜单管理 REST 控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    /**
     * 获取租户下的所有菜单（树形结构）
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> getMenusByTenantId(@PathVariable String tenantId) {
        log.info("获取租户菜单: tenantId={}", tenantId);

        List<MenuDto.MenuView> menus = menuApplicationService.findMenusByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    /**
     * 获取租户下的根菜单
     */
    @GetMapping("/tenant/{tenantId}/roots")
    public ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> getRootMenusByTenantId(@PathVariable String tenantId) {
        log.info("获取租户根菜单: tenantId={}", tenantId);

        List<MenuDto.MenuView> menus = menuApplicationService.findRootMenusByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    /**
     * 根据ID获取菜单详情
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> getMenuById(@PathVariable String menuId) {
        log.info("获取菜单详情: id={}", menuId);

        Optional<MenuDto.MenuView> menu = menuApplicationService.findMenuById(menuId);
        if (menu.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(menu.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("菜单不存在"));
        }
    }

    /**
     * 创建菜单
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> createMenu(@RequestBody MenuDto.CreateMenuCommand command) {
        log.info("创建菜单: name={}, tenantId={}", command.getName(), command.getTenantId());

        try {
            MenuDto.MenuView menu = menuApplicationService.createMenu(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(menu, "菜单创建成功"));
        } catch (Exception e) {
            log.error("创建菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> updateMenu(
            @PathVariable String menuId,
            @RequestBody MenuDto.UpdateMenuCommand command) {
        log.info("更新菜单: id={}, name={}", menuId, command.getName());

        try {
            MenuDto.MenuView menu = menuApplicationService.updateMenu(menuId, command);
            return ResponseEntity.ok(ApiResponse.success(menu, "菜单更新成功"));
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable String menuId) {
        log.info("删除菜单: id={}", menuId);

        try {
            menuApplicationService.deleteMenu(menuId);
            return ResponseEntity.ok(ApiResponse.success(null, "菜单删除成功"));
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
