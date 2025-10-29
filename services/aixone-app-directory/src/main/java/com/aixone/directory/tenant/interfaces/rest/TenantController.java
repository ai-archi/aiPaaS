package com.aixone.directory.tenant.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.tenant.application.TenantApplicationService;
import com.aixone.directory.tenant.application.TenantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户 REST 控制器
 * 提供租户管理的 HTTP API
 */
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantApplicationService tenantApplicationService;

    /**
     * 获取租户列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<TenantDto.TenantView>>> getTenants(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        log.info("查询租户列表: pageNum={}, pageSize={}, name={}, status={}", pageNum, pageSize, name, status);
        
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<TenantDto.TenantView> result = tenantApplicationService.findTenants(pageRequest, name, status);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 根据ID获取租户
     */
    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantDto.TenantView>> getTenantById(@PathVariable String tenantId) {
        log.info("查询租户: id={}", tenantId);
        
        TenantDto.TenantView dto = tenantApplicationService.findTenantById(tenantId)
                .orElse(null);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("租户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /**
     * 创建租户
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TenantDto.TenantView>> createTenant(@RequestBody TenantDto.CreateTenantCommand command) {
        log.info("创建租户: name={}, groupId={}", command.getName(), command.getGroupId());
        
        try {
            TenantDto.TenantView dto = tenantApplicationService.createTenant(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(dto, "租户创建成功"));
        } catch (Exception e) {
            log.error("创建租户失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 更新租户
     */
    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantDto.TenantView>> updateTenant(
            @PathVariable String tenantId,
            @RequestBody TenantDto.UpdateTenantCommand command) {
        log.info("更新租户: id={}, name={}", tenantId, command.getName());
        
        try {
            TenantDto.TenantView dto = tenantApplicationService.updateTenant(tenantId, command);
            return ResponseEntity.ok(ApiResponse.success(dto, "租户更新成功"));
        } catch (Exception e) {
            log.error("更新租户失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable String tenantId) {
        log.info("删除租户: id={}", tenantId);
        
        try {
            tenantApplicationService.deleteTenant(tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "租户删除成功"));
        } catch (Exception e) {
            log.error("删除租户失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 批量删除租户
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<ApiResponse<Void>> batchDeleteTenants(@RequestBody List<String> tenantIds) {
        log.info("批量删除租户: ids={}", tenantIds);
        
        try {
            tenantApplicationService.deleteTenants(tenantIds);
            return ResponseEntity.ok(ApiResponse.success(null, "租户批量删除成功"));
        } catch (Exception e) {
            log.error("批量删除租户失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }
} 