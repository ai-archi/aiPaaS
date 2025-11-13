package com.aixone.directory.group.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.group.application.GroupApplicationService;
import com.aixone.directory.group.application.dto.CreateGroupRequest;
import com.aixone.directory.group.application.dto.GroupDto;
import com.aixone.directory.group.application.dto.ReplaceMembersRequest;
import com.aixone.directory.group.application.dto.ReplaceRolesRequest;
import com.aixone.directory.group.application.dto.UpdateGroupRequest;
import com.aixone.directory.user.application.UserDto;
import com.aixone.directory.role.application.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 群组管理 Controller
 * 支持群组的CRUD操作和成员/角色管理，租户ID从token自动获取
 * 路径: /api/v1/groups
 */
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupQueryController {

    private final GroupApplicationService groupApplicationService;

    /**
     * 获取群组列表（分页，支持过滤）
     * 支持两种分页参数名：pageNum/pageSize 和 page/limit（兼容前端baTable）
     * 租户ID从token自动获取
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<GroupDto>>> getGroups(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String name) {
        
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        // 兼容两种参数名：优先使用 pageNum/pageSize，如果未提供则使用 page/limit
        int actualPageNum = (pageNum != null) ? pageNum : ((page != null) ? page : 1);
        int actualPageSize = (pageSize != null) ? pageSize : ((limit != null) ? limit : 20);
        
        log.info("查询群组列表: pageNum={}, pageSize={}, tenantId={}, name={}", 
                actualPageNum, actualPageSize, tenantId, name);
        
        try {
            PageRequest pageRequest = new PageRequest(actualPageNum, actualPageSize);
            PageResult<GroupDto> result = groupApplicationService.findGroups(
                    pageRequest, tenantId, name);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("查询群组列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "查询群组列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取群组详情
     * 租户ID从token自动获取
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<RowData<GroupDto>>> getGroupById(@PathVariable String groupId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("获取群组详情: groupId={}, tenantId={}", groupId, tenantId);
        
        try {
            GroupDto group = groupApplicationService.getGroup(tenantId, groupId);
            // baTable期望的格式：{code: 200, data: {row: {...}}}
            RowData<GroupDto> rowData = new RowData<>();
            rowData.setRow(group);
            return ResponseEntity.ok(ApiResponse.success(rowData));
        } catch (Exception e) {
            log.error("获取群组详情失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "群组不存在: " + e.getMessage()));
        }
    }

    /**
     * 创建群组
     * 租户ID从token自动获取并设置
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GroupDto>> createGroup(@RequestBody CreateGroupRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("创建群组: name={}, tenantId={}", request.getName(), tenantId);
        
        try {
            GroupDto group = groupApplicationService.createGroup(tenantId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(group, "群组创建成功"));
        } catch (Exception e) {
            log.error("创建群组失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "创建群组失败: " + e.getMessage()));
        }
    }

    /**
     * 更新群组
     * 租户ID从token自动获取，自动验证群组是否属于当前租户
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDto>> updateGroup(
            @PathVariable String groupId,
            @RequestBody UpdateGroupRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("更新群组: groupId={}, tenantId={}, name={}", groupId, tenantId, request.getName());
        
        try {
            GroupDto group = groupApplicationService.updateGroup(tenantId, groupId, request);
            return ResponseEntity.ok(ApiResponse.success(group, "群组更新成功"));
        } catch (Exception e) {
            log.error("更新群组失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "更新群组失败: " + e.getMessage()));
        }
    }

    /**
     * 删除群组
     * 租户ID从token自动获取，自动验证群组是否属于当前租户
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String groupId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("删除群组: groupId={}, tenantId={}", groupId, tenantId);
        
        try {
            groupApplicationService.deleteGroup(tenantId, groupId);
            return ResponseEntity.ok(ApiResponse.success(null, "群组删除成功"));
        } catch (Exception e) {
            log.error("删除群组失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "删除群组失败: " + e.getMessage()));
        }
    }

    /**
     * 获取群组成员列表
     * 租户ID从token自动获取
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<UserDto>>> getGroupMembers(@PathVariable String groupId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("获取群组成员列表: groupId={}, tenantId={}", groupId, tenantId);
        
        try {
            List<UserDto> members = groupApplicationService.getGroupMembers(tenantId, groupId);
            return ResponseEntity.ok(ApiResponse.success(members));
        } catch (Exception e) {
            log.error("获取群组成员列表失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "获取群组成员列表失败: " + e.getMessage()));
        }
    }

    /**
     * 更新群组成员集合（批量替换）
     * 租户ID从token自动获取
     */
    @PutMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<Void>> replaceGroupMembers(
            @PathVariable String groupId,
            @RequestBody ReplaceMembersRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("批量替换群组成员: groupId={}, tenantId={}, userIds={}", groupId, tenantId, request.getUserIds());
        
        try {
            groupApplicationService.replaceGroupMembers(tenantId, groupId, request.getUserIds());
            return ResponseEntity.ok(ApiResponse.success(null, "群组成员更新成功"));
        } catch (Exception e) {
            log.error("更新群组成员失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "更新群组成员失败: " + e.getMessage()));
        }
    }

    /**
     * 添加成员到群组
     * 租户ID从token自动获取
     */
    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> addMemberToGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("添加成员到群组: groupId={}, userId={}, tenantId={}", groupId, userId, tenantId);
        
        try {
            groupApplicationService.addMemberToGroup(tenantId, groupId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "成员添加成功"));
        } catch (Exception e) {
            log.error("添加成员失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "添加成员失败: " + e.getMessage()));
        }
    }

    /**
     * 从群组移除成员
     * 租户ID从token自动获取
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMemberFromGroup(
            @PathVariable String groupId,
            @PathVariable String userId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("从群组移除成员: groupId={}, userId={}, tenantId={}", groupId, userId, tenantId);
        
        try {
            groupApplicationService.removeMemberFromGroup(tenantId, groupId, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "成员移除成功"));
        } catch (Exception e) {
            log.error("移除成员失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "移除成员失败: " + e.getMessage()));
        }
    }

    /**
     * 获取群组的角色列表
     * 租户ID从token自动获取
     */
    @GetMapping("/{groupId}/roles")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getGroupRoles(@PathVariable String groupId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("获取群组角色列表: groupId={}, tenantId={}", groupId, tenantId);
        
        try {
            List<RoleDto> roles = groupApplicationService.getGroupRoles(tenantId, groupId);
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            log.error("获取群组角色列表失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "获取群组角色列表失败: " + e.getMessage()));
        }
    }

    /**
     * 更新群组的角色集合（批量替换）
     * 租户ID从token自动获取
     */
    @PutMapping("/{groupId}/roles")
    public ResponseEntity<ApiResponse<Void>> replaceGroupRoles(
            @PathVariable String groupId,
            @RequestBody ReplaceRolesRequest request) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("批量替换群组角色: groupId={}, tenantId={}, roleIds={}", groupId, tenantId, request.getRoleIds());
        
        try {
            groupApplicationService.replaceGroupRoles(tenantId, groupId, request.getRoleIds());
            return ResponseEntity.ok(ApiResponse.success(null, "群组角色更新成功"));
        } catch (Exception e) {
            log.error("更新群组角色失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "更新群组角色失败: " + e.getMessage()));
        }
    }

    /**
     * 分配角色给群组
     * 租户ID从token自动获取
     */
    @PutMapping("/{groupId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> addRoleToGroup(
            @PathVariable String groupId,
            @PathVariable String roleId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("分配角色给群组: groupId={}, roleId={}, tenantId={}", groupId, roleId, tenantId);
        
        try {
            groupApplicationService.addRoleToGroup(tenantId, groupId, roleId);
            return ResponseEntity.ok(ApiResponse.success(null, "角色分配成功"));
        } catch (Exception e) {
            log.error("分配角色失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "分配角色失败: " + e.getMessage()));
        }
    }

    /**
     * 移除群组的角色
     * 租户ID从token自动获取
     */
    @DeleteMapping("/{groupId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromGroup(
            @PathVariable String groupId,
            @PathVariable String roleId) {
        // 从token获取租户ID
        String tenantId = SessionContext.getTenantId();
        if (!StringUtils.hasText(tenantId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未提供有效的租户信息"));
        }
        
        log.info("移除群组角色: groupId={}, roleId={}, tenantId={}", groupId, roleId, tenantId);
        
        try {
            groupApplicationService.removeRoleFromGroup(tenantId, groupId, roleId);
            return ResponseEntity.ok(ApiResponse.success(null, "角色移除成功"));
        } catch (Exception e) {
            log.error("移除角色失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "移除角色失败: " + e.getMessage()));
        }
    }
}

