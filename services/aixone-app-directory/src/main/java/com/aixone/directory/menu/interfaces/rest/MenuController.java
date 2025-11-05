package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
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
     * 获取菜单列表（分页，支持过滤和排序）
     * 支持两种分页参数名：pageNum/pageSize 和 page/limit（兼容前端baTable）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> getMenus(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String order) {
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询菜单列表: pageNum={}, pageSize={}, page={}, limit={}, tenantId={}, name={}, title={}, type={}, order={}", 
                pageNum, pageSize, page, limit, tenantId, name, title, type, order);
        
        // 解析排序参数（格式：field,direction，例如：updatedAt,desc）
        String sortBy = null;
        String sortDirection = "asc";
        if (order != null && !order.isEmpty()) {
            String[] orderParts = order.split(",");
            if (orderParts.length >= 1) {
                sortBy = orderParts[0].trim();
            }
            if (orderParts.length >= 2) {
                sortDirection = orderParts[1].trim().toLowerCase();
                // 标准化排序方向：ascending/descending -> asc/desc
                if ("ascending".equals(sortDirection)) {
                    sortDirection = "asc";
                } else if ("descending".equals(sortDirection)) {
                    sortDirection = "desc";
                }
            }
        }
        
        PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize, sortBy, sortDirection);
        PageResult<MenuDto.MenuView> result = menuApplicationService.findMenus(
                pageRequest, tenantId, name, title, type);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取租户下的所有菜单（树形结构）
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> getMenusByTenantId(@PathVariable String tenantId) {
        log.info("获取租户菜单: tenantId={}", tenantId);

        // 处理特殊 tenantId 值
        String actualTenantId = convertTenantId(tenantId);
        List<MenuDto.MenuView> menus = menuApplicationService.findMenusByTenantId(actualTenantId);
        return ResponseEntity.ok(ApiResponse.success(menus));
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
     * 获取租户下的根菜单
     */
    @GetMapping("/tenant/{tenantId}/roots")
    public ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> getRootMenusByTenantId(@PathVariable String tenantId) {
        log.info("获取租户根菜单: tenantId={}", tenantId);

        // 处理特殊 tenantId 值
        String actualTenantId = convertTenantId(tenantId);
        List<MenuDto.MenuView> menus = menuApplicationService.findRootMenusByTenantId(actualTenantId);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    /**
     * 根据ID获取菜单详情
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<RowData<MenuDto.MenuView>>> getMenuById(@PathVariable String menuId) {
        log.info("获取菜单详情: id={}", menuId);

        Optional<MenuDto.MenuView> menu = menuApplicationService.findMenuById(menuId);
        if (menu.isPresent()) {
            // baTable期望的格式：{code: 200, data: {row: {...}}}
            RowData<MenuDto.MenuView> rowData = new RowData<>(menu.get());
            return ResponseEntity.ok(ApiResponse.success(rowData));
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
