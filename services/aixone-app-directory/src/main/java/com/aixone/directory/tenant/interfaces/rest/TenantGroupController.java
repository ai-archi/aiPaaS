package com.aixone.directory.tenant.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.directory.tenant.application.TenantGroupApplicationService;
import com.aixone.directory.tenant.application.dto.TenantGroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 租户组控制器
 */
@RestController
@RequestMapping("/api/v1/tenant-groups")
@RequiredArgsConstructor
@Slf4j
public class TenantGroupController {

    private final TenantGroupApplicationService tenantGroupApplicationService;

    /**
     * 创建租户组
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TenantGroupDto>> createTenantGroup(
            @RequestBody TenantGroupDto.CreateTenantGroupRequest request) {
        log.info("创建租户组: name={}", request.getName());
        TenantGroupDto dto = tenantGroupApplicationService.createTenantGroup(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "租户组创建成功"));
    }

    /**
     * 获取租户组详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantGroupDto>> getTenantGroup(@PathVariable String id) {
        Optional<TenantGroupDto> dto = tenantGroupApplicationService.getTenantGroup(id);
        return dto.map(tenantGroupDto -> ResponseEntity.ok(ApiResponse.success(tenantGroupDto)))
                .orElse(ResponseEntity.ok(ApiResponse.notFound("租户组不存在")));
    }

    /**
     * 获取所有租户组
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantGroupDto>>> getAllTenantGroups() {
        List<TenantGroupDto> groups = tenantGroupApplicationService.getAllTenantGroups();
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    /**
     * 根据父ID获取租户组
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<TenantGroupDto>>> getTenantGroupsByParent(@PathVariable String parentId) {
        List<TenantGroupDto> groups = tenantGroupApplicationService.getTenantGroupsByParent(parentId);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    /**
     * 获取根租户组
     */
    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<TenantGroupDto>>> getRootTenantGroups() {
        List<TenantGroupDto> groups = tenantGroupApplicationService.getRootTenantGroups();
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    /**
     * 更新租户组
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantGroupDto>> updateTenantGroup(
            @PathVariable String id,
            @RequestBody TenantGroupDto.UpdateTenantGroupRequest request) {
        log.info("更新租户组: id={}", id);
        TenantGroupDto dto = tenantGroupApplicationService.updateTenantGroup(id, request);
        return ResponseEntity.ok(ApiResponse.success(dto, "租户组更新成功"));
    }

    /**
     * 删除租户组
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenantGroup(@PathVariable String id) {
        log.info("删除租户组: id={}", id);
        tenantGroupApplicationService.deleteTenantGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "租户组删除成功"));
    }

    /**
     * 激活租户组
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateTenantGroup(@PathVariable String id) {
        log.info("激活租户组: id={}", id);
        tenantGroupApplicationService.activateTenantGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "租户组激活成功"));
    }

    /**
     * 停用租户组
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateTenantGroup(@PathVariable String id) {
        log.info("停用租户组: id={}", id);
        tenantGroupApplicationService.deactivateTenantGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "租户组停用成功"));
    }
}

