package com.aixone.directory.user.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.user.application.UserApplicationService;
import com.aixone.directory.user.application.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户查询 Controller
 * 支持分页查询用户列表，租户ID从token自动获取
 * 路径: /api/v1/users
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserQueryController {

    private final UserApplicationService userApplicationService;

    /**
     * 获取用户列表（分页，支持过滤）
     * 支持两种分页参数名：pageNum/pageSize 和 page/limit（兼容前端baTable）
     * 租户ID从token自动获取
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<UserDto>>> getUsers(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status) {
        
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询用户列表: pageNum={}, pageSize={}, tenantId={}, username={}, email={}, status={}", 
                actualPageNum, actualPageSize, tenantId, username, email, status);
        
        try {
            PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize);
            PageResult<UserDto> result = userApplicationService.findUsers(
                    pageRequest, tenantId, username, email, status);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户详情
     * 租户ID从token自动获取
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<RowData<UserDto>>> getUserById(@PathVariable String userId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("获取用户详情: userId={}, tenantId={}", userId, tenantId);
        
        try {
            UserDto user = userApplicationService.getUser(tenantId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            // baTable期望的格式：{code: 200, data: {row: {...}}}
            RowData<UserDto> rowData = new RowData<>();
            rowData.setRow(user);
            return ResponseEntity.ok(ApiResponse.success(rowData));
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "用户不存在: " + e.getMessage()));
        }
    }
}

