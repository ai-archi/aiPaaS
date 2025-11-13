package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
import com.aixone.common.session.SessionContext;
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
     * 获取菜单列表
     * 如果 isTree=true，返回树形结构数据（不分页）
     * 否则返回分页数据
     * 租户ID从token自动获取
     */
    @GetMapping
    public ResponseEntity<?> getMenus(
            @RequestParam(required = false, defaultValue = "true") Boolean isTree,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String quickSearch) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        try {
            // 如果 isTree=true，返回树形结构数据（参考 buildadmin 格式）
            if (Boolean.TRUE.equals(isTree)) {
                log.info("查询菜单列表（树形结构）: tenantId={}, name={}, title={}, type={}, quickSearch={}", 
                        tenantId, name, title, type, quickSearch);
                
                List<MenuDto.MenuView> treeMenus = menuApplicationService.findMenusTree(
                        tenantId, name, title, type, quickSearch);
                
                // 调试：打印树形数据结构
                log.debug("返回的树形菜单数据数量: {}", treeMenus.size());
                if (!treeMenus.isEmpty()) {
                    log.debug("第一个根菜单: id={}, title={}, children数量={}", 
                            treeMenus.get(0).getId(), 
                            treeMenus.get(0).getTitle(),
                            treeMenus.get(0).getChildren() != null ? treeMenus.get(0).getChildren().size() : 0);
                }
                
                // 返回格式：{ code: 200, data: { list: [...], remark: '...' } }
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("list", treeMenus);
                data.put("remark", "");
                
                return ResponseEntity.ok(ApiResponse.success(data));
            } else {
                // 分页查询
                int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
                int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
                
                log.info("查询菜单列表（分页）: pageNum={}, pageSize={}, tenantId={}, parentId={}, name={}, title={}, type={}, order={}", 
                        actualPageNum, actualPageSize, tenantId, parentId, name, title, type, order);
                
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
                        pageRequest, tenantId, parentId, name, title, type);
                
                return ResponseEntity.ok(ApiResponse.success(result));
            }
        } catch (Exception e) {
            log.error("查询菜单列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询菜单列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取菜单详情
     * 租户ID从token自动获取，自动验证菜单是否属于当前租户
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<RowData<MenuDto.MenuView>>> getMenuById(@PathVariable String menuId) {
        log.info("获取菜单详情: id={}", menuId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        Optional<MenuDto.MenuView> menu = menuApplicationService.findMenuById(menuId, tenantId);
        if (menu.isPresent()) {
            // baTable期望的格式：{code: 200, data: {row: {...}}}
            RowData<MenuDto.MenuView> rowData = new RowData<>();
            rowData.setRow(menu.get());
            return ResponseEntity.ok(ApiResponse.success(rowData));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("菜单不存在或不属于当前租户"));
        }
    }

    /**
     * 获取菜单的子菜单
     * 租户ID从token自动获取
     */
    @GetMapping("/{menuId}/children")
    public ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> getMenuChildren(@PathVariable String menuId) {
        log.info("获取菜单子菜单: menuId={}", menuId);

        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }

        List<MenuDto.MenuView> children = menuApplicationService.findMenuChildren(menuId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(children));
    }


    /**
     * 创建菜单
     * 租户ID从token自动获取并设置
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> createMenu(@RequestBody MenuDto.CreateMenuCommand command) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 设置租户ID（覆盖请求体中的tenantId，确保安全）
        command.setTenantId(tenantId);
        
        log.info("创建菜单: name={}, tenantId={}", command.getName(), tenantId);

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
     * 租户ID从token自动获取，自动验证菜单是否属于当前租户
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuDto.MenuView>> updateMenu(
            @PathVariable String menuId,
            @RequestBody MenuDto.UpdateMenuCommand command) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("更新菜单: id={}, tenantId={}, name={}", menuId, tenantId, command.getName());

        try {
            MenuDto.MenuView menu = menuApplicationService.updateMenu(menuId, tenantId, command);
            return ResponseEntity.ok(ApiResponse.success(menu, "菜单更新成功"));
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 删除菜单
     * 租户ID从token自动获取，自动验证菜单是否属于当前租户
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable String menuId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("删除菜单: id={}, tenantId={}", menuId, tenantId);

        try {
            menuApplicationService.deleteMenu(menuId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "菜单删除成功"));
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

}
