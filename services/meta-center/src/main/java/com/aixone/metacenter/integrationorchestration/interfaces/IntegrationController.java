package com.aixone.metacenter.integrationorchestration.interfaces;

import com.aixone.metacenter.common.response.ApiResponse;
import com.aixone.metacenter.integrationorchestration.application.IntegrationApplicationService;
import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationDTO;
import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 集成编排REST控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationApplicationService integrationApplicationService;

    /**
     * 创建集成配置
     * 
     * @param integrationDTO 集成配置DTO
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<IntegrationDTO>> createIntegration(@Valid @RequestBody IntegrationDTO integrationDTO) {
        log.info("创建集成配置: {}", integrationDTO.getName());
        IntegrationDTO created = integrationApplicationService.createIntegration(integrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "集成配置创建成功"));
    }

    /**
     * 根据ID查询集成配置
     * 
     * @param id 集成配置ID
     * @return 集成配置
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IntegrationDTO>> getIntegrationById(@PathVariable Long id) {
        log.info("根据ID查询集成配置: {}", id);
        IntegrationDTO integration = integrationApplicationService.getIntegrationById(id);
        return ResponseEntity.ok(ApiResponse.success(integration, "查询成功"));
    }

    /**
     * 根据租户ID分页查询集成配置
     * 
     * @param tenantId 租户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/tenant/{tenantId}/page")
    public ResponseEntity<ApiResponse<Page<IntegrationDTO>>> getIntegrationsByTenantIdPage(
            @PathVariable String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据租户ID分页查询集成配置: tenantId={}, page={}, size={}", tenantId, page, size);
        Page<IntegrationDTO> result = integrationApplicationService.getIntegrationsByTenantId(tenantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result, "查询成功"));
    }

    /**
     * 更新集成配置
     * 
     * @param id 集成配置ID
     * @param integrationDTO 集成配置DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IntegrationDTO>> updateIntegration(
            @PathVariable Long id, 
            @Valid @RequestBody IntegrationDTO integrationDTO) {
        log.info("更新集成配置: id={}, name={}", id, integrationDTO.getName());
        IntegrationDTO updated = integrationApplicationService.updateIntegration(id, integrationDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "集成配置更新成功"));
    }

    /**
     * 删除集成配置
     * 
     * @param id 集成配置ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIntegration(@PathVariable Long id) {
        log.info("删除集成配置: {}", id);
        integrationApplicationService.deleteIntegration(id);
        return ResponseEntity.ok(ApiResponse.success(null, "集成配置删除成功"));
    }



}
