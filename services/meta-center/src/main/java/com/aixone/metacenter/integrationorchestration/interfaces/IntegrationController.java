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
     * 根据租户ID和名称查询集成配置
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 集成配置
     */
    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<IntegrationDTO>> getIntegrationByName(
            @RequestParam String tenantId, 
            @RequestParam String name) {
        log.info("根据租户ID和名称查询集成配置: tenantId={}, name={}", tenantId, name);
        IntegrationDTO integration = integrationApplicationService.getIntegrationByName(tenantId, name);
        return ResponseEntity.ok(ApiResponse.success(integration, "查询成功"));
    }

    /**
     * 分页查询集成配置
     * 
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<IntegrationDTO>>> getIntegrations(
            IntegrationQuery query, 
            Pageable pageable) {
        log.info("分页查询集成配置: query={}, pageable={}", query, pageable);
        Page<IntegrationDTO> result = integrationApplicationService.getIntegrations(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "查询成功"));
    }

    /**
     * 根据租户ID查询集成配置列表
     * 
     * @param tenantId 租户ID
     * @return 集成配置列表
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<IntegrationDTO>>> getIntegrationsByTenantId(@PathVariable String tenantId) {
        log.info("根据租户ID查询集成配置列表: {}", tenantId);
        List<IntegrationDTO> integrations = integrationApplicationService.getIntegrationsByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(integrations, "查询成功"));
    }

    /**
     * 根据租户ID和集成类型查询集成配置列表
     * 
     * @param tenantId 租户ID
     * @param integrationType 集成类型
     * @return 集成配置列表
     */
    @GetMapping("/tenant/{tenantId}/type/{integrationType}")
    public ResponseEntity<ApiResponse<List<IntegrationDTO>>> getIntegrationsByTenantIdAndType(
            @PathVariable String tenantId, 
            @PathVariable String integrationType) {
        log.info("根据租户ID和集成类型查询集成配置列表: tenantId={}, integrationType={}", tenantId, integrationType);
        List<IntegrationDTO> integrations = integrationApplicationService.getIntegrationsByTenantIdAndType(tenantId, integrationType);
        return ResponseEntity.ok(ApiResponse.success(integrations, "查询成功"));
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

    /**
     * 根据租户ID删除集成配置
     * 
     * @param tenantId 租户ID
     * @return 删除结果
     */
    @DeleteMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteIntegrationsByTenantId(@PathVariable String tenantId) {
        log.info("根据租户ID删除集成配置: {}", tenantId);
        integrationApplicationService.deleteIntegrationsByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "集成配置删除成功"));
    }

    /**
     * 启用集成配置
     * 
     * @param id 集成配置ID
     * @return 启用结果
     */
    @PostMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<IntegrationDTO>> enableIntegration(@PathVariable Long id) {
        log.info("启用集成配置: {}", id);
        IntegrationDTO enabled = integrationApplicationService.enableIntegration(id);
        return ResponseEntity.ok(ApiResponse.success(enabled, "集成配置启用成功"));
    }

    /**
     * 禁用集成配置
     * 
     * @param id 集成配置ID
     * @return 禁用结果
     */
    @PostMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<IntegrationDTO>> disableIntegration(@PathVariable Long id) {
        log.info("禁用集成配置: {}", id);
        IntegrationDTO disabled = integrationApplicationService.disableIntegration(id);
        return ResponseEntity.ok(ApiResponse.success(disabled, "集成配置禁用成功"));
    }

    /**
     * 测试集成连接
     * 
     * @param id 集成配置ID
     * @return 测试结果
     */
    @PostMapping("/{id}/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testIntegration(@PathVariable Long id) {
        log.info("测试集成连接: {}", id);
        Map<String, Object> result = integrationApplicationService.testIntegration(id);
        return ResponseEntity.ok(ApiResponse.success(result, "集成连接测试完成"));
    }

    /**
     * 执行集成任务
     * 
     * @param id 集成配置ID
     * @param data 执行数据
     * @return 执行结果
     */
    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<Object>> executeIntegration(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        log.info("执行集成任务: id={}", id);
        Object result = integrationApplicationService.executeIntegration(id, data);
        return ResponseEntity.ok(ApiResponse.success(result, "集成任务执行成功"));
    }

    /**
     * 获取集成执行日志
     * 
     * @param id 集成配置ID
     * @param limit 限制条数
     * @return 执行日志
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getIntegrationLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("获取集成执行日志: id={}, limit={}", id, limit);
        List<Map<String, Object>> logs = integrationApplicationService.getIntegrationLogs(id, limit);
        return ResponseEntity.ok(ApiResponse.success(logs, "获取日志成功"));
    }

    /**
     * 获取集成统计信息
     * 
     * @param id 集成配置ID
     * @return 统计信息
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getIntegrationStats(@PathVariable Long id) {
        log.info("获取集成统计信息: {}", id);
        Map<String, Object> stats = integrationApplicationService.getIntegrationStats(id);
        return ResponseEntity.ok(ApiResponse.success(stats, "获取统计信息成功"));
    }

    /**
     * 同步外部系统数据
     * 
     * @param id 集成配置ID
     * @param syncConfig 同步配置
     * @return 同步结果
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncExternalData(
            @PathVariable Long id,
            @RequestBody Map<String, Object> syncConfig) {
        log.info("同步外部系统数据: id={}", id);
        Map<String, Object> result = integrationApplicationService.syncExternalData(id, syncConfig);
        return ResponseEntity.ok(ApiResponse.success(result, "数据同步成功"));
    }

    /**
     * 获取支持的协议类型
     * 
     * @return 协议类型列表
     */
    @GetMapping("/protocols")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedProtocols() {
        log.info("获取支持的协议类型");
        List<String> protocols = integrationApplicationService.getSupportedProtocols();
        return ResponseEntity.ok(ApiResponse.success(protocols, "获取协议类型成功"));
    }

    /**
     * 获取集成模板列表
     * 
     * @return 模板列表
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getIntegrationTemplates() {
        log.info("获取集成模板列表");
        List<Map<String, Object>> templates = integrationApplicationService.getIntegrationTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates, "获取模板列表成功"));
    }

    /**
     * 根据模板创建集成配置
     * 
     * @param templateId 模板ID
     * @param config 配置参数
     * @return 创建结果
     */
    @PostMapping("/templates/{templateId}/create")
    public ResponseEntity<ApiResponse<IntegrationDTO>> createFromTemplate(
            @PathVariable String templateId,
            @RequestBody Map<String, Object> config) {
        log.info("根据模板创建集成配置: templateId={}", templateId);
        IntegrationDTO created = integrationApplicationService.createFromTemplate(templateId, config);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "集成配置创建成功"));
    }
}
