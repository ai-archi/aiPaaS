package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.menu.application.MenuApplicationService;
import com.aixone.directory.menu.application.MenuDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 菜单管理 REST 控制器（管理员接口，支持跨租户操作）
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/admin/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuAdminController {

    private final MenuApplicationService menuApplicationService;

    /**
     * 管理员查询菜单列表（可跨租户）
     * tenantId通过查询参数传递
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> getMenus(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "tenantId参数不能为空"));
        }
        
        int actualPageNum = (pageNum != null) ? pageNum : 1;
        int actualPageSize = (pageSize != null) ? pageSize : 20;
        
        log.info("管理员查询菜单列表: tenantId={}, parentId={}, pageNum={}, pageSize={}, name={}, title={}, type={}", 
                tenantId, parentId, actualPageNum, actualPageSize, name, title, type);
        
        PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize);
        PageResult<MenuDto.MenuView> result = menuApplicationService.findMenus(
                pageRequest, tenantId, parentId, name, title, type);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 管理员查询菜单详情（可跨租户）
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> getMenuById(
            @PathVariable String menuId,
            @RequestParam(required = false) String tenantId) {
        log.info("管理员查询菜单详情: menuId={}, tenantId={}", menuId, tenantId);

        Optional<MenuDto.MenuView> menu = menuApplicationService.findMenuById(menuId);
        if (menu.isPresent()) {
            // 如果提供了tenantId，验证菜单是否属于该租户
            if (StringUtils.hasText(tenantId) && !menu.get().getTenantId().equals(tenantId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("菜单不存在或不属于指定租户"));
            }
            return ResponseEntity.ok(ApiResponse.success(menu.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("菜单不存在"));
        }
    }

    /**
     * 管理员创建菜单
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> createMenu(
            @RequestParam(required = false) String tenantId,
            @RequestBody MenuDto.CreateMenuCommand command) {
        
        // tenantId可以从查询参数或请求体获取，优先使用查询参数
        if (StringUtils.hasText(tenantId)) {
            command.setTenantId(tenantId);
        }
        
        if (!StringUtils.hasText(command.getTenantId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "tenantId不能为空"));
        }
        
        log.info("管理员创建菜单: name={}, tenantId={}", command.getName(), command.getTenantId());

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
     * 管理员更新菜单（可跨租户）
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> updateMenu(
            @PathVariable String menuId,
            @RequestParam(required = false) String tenantId,
            @RequestBody MenuDto.UpdateMenuCommand command) {
        log.info("管理员更新菜单: menuId={}, tenantId={}, name={}", menuId, tenantId, command.getName());

        try {
            // 管理员接口：使用不带租户验证的更新方法（可以跨租户更新）
            MenuDto.MenuView menu = menuApplicationService.updateMenu(menuId, command);
            return ResponseEntity.ok(ApiResponse.success(menu, "菜单更新成功"));
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 管理员删除菜单（可跨租户）
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @PathVariable String menuId,
            @RequestParam(required = false) String tenantId) {
        log.info("管理员删除菜单: menuId={}, tenantId={}", menuId, tenantId);

        try {
            // 管理员接口：使用不带租户验证的删除方法（可以跨租户删除）
            menuApplicationService.deleteMenu(menuId);
            return ResponseEntity.ok(ApiResponse.success(null, "菜单删除成功"));
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }
}

