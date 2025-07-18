package com.aixone.metacenter.integrationorchestration.interfaces;

import com.aixone.metacenter.common.response.ApiResponse;
import com.aixone.metacenter.integrationorchestration.application.IntegrationApplicationService;
import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationDTO;
import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    @Autowired
    private IntegrationApplicationService integrationApplicationService;

    @PostMapping
    public ApiResponse<IntegrationDTO> createIntegration(@RequestBody IntegrationDTO integrationDTO) {
        try {
            IntegrationDTO created = integrationApplicationService.createIntegration(integrationDTO);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error("创建集成配置失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<IntegrationDTO> updateIntegration(@PathVariable Long id, @RequestBody IntegrationDTO integrationDTO) {
        try {
            IntegrationDTO updated = integrationApplicationService.updateIntegration(id, integrationDTO);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error("更新集成配置失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteIntegration(@PathVariable Long id) {
        try {
            integrationApplicationService.deleteIntegration(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除集成配置失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<IntegrationDTO> getIntegrationById(@PathVariable Long id) {
        try {
            IntegrationDTO integration = integrationApplicationService.getIntegrationById(id);
            return ApiResponse.success(integration);
        } catch (Exception e) {
            return ApiResponse.error("获取集成配置失败: " + e.getMessage());
        }
    }

    @GetMapping("/by-tenant/{tenantId}")
    public ApiResponse<List<IntegrationDTO>> getIntegrationsByTenantId(@PathVariable String tenantId) {
        try {
            List<IntegrationDTO> integrations = integrationApplicationService.getIntegrationsByTenantId(tenantId);
            return ApiResponse.success(integrations);
        } catch (Exception e) {
            return ApiResponse.error("获取集成配置列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/by-status/{status}")
    public ApiResponse<List<IntegrationDTO>> getIntegrationsByStatus(@PathVariable String status) {
        try {
            List<IntegrationDTO> integrations = integrationApplicationService.getIntegrationsByStatus(status);
            return ApiResponse.success(integrations);
        } catch (Exception e) {
            return ApiResponse.error("获取集成配置列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/by-type/{integrationType}")
    public ApiResponse<List<IntegrationDTO>> getIntegrationsByType(@PathVariable String integrationType) {
        try {
            List<IntegrationDTO> integrations = integrationApplicationService.getIntegrationsByType(integrationType);
            return ApiResponse.success(integrations);
        } catch (Exception e) {
            return ApiResponse.error("获取集成配置列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/search")
    public ApiResponse<Page<IntegrationDTO>> searchIntegrations(
            @RequestBody IntegrationQuery query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<IntegrationDTO> result = integrationApplicationService.getIntegrations(query, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("查询集成配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/execute")
    public ApiResponse<String> executeIntegration(@PathVariable Long id) {
        try {
            String result = integrationApplicationService.executeIntegration(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("执行集成失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/test-connection")
    public ApiResponse<String> testIntegrationConnection(@PathVariable Long id) {
        try {
            String result = integrationApplicationService.testIntegrationConnection(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("测试连接失败: " + e.getMessage());
        }
    }
}
