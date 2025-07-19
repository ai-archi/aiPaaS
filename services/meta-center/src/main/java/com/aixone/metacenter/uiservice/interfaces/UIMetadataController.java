package com.aixone.metacenter.uiservice.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.metacenter.uiservice.application.UIMetadataApplicationService;
import com.aixone.metacenter.uiservice.application.dto.UIMetadataDTO;
import com.aixone.metacenter.uiservice.application.dto.UIMetadataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UI元数据控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@RestController
@RequestMapping("/api/v1/ui-metadata")
public class UIMetadataController {

    @Autowired
    private UIMetadataApplicationService uiMetadataApplicationService;

    /**
     * 创建UI元数据
     * 
     * @param uiMetadataDTO UI元数据DTO
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<UIMetadataDTO> createUIMetadata(@RequestBody UIMetadataDTO uiMetadataDTO) {
        try {
            UIMetadataDTO created = uiMetadataApplicationService.createUIMetadata(uiMetadataDTO);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error("创建UI元数据失败: " + e.getMessage());
        }
    }

    /**
     * 更新UI元数据
     * 
     * @param id UI元数据ID
     * @param uiMetadataDTO UI元数据DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<UIMetadataDTO> updateUIMetadata(@PathVariable Long id, @RequestBody UIMetadataDTO uiMetadataDTO) {
        try {
            UIMetadataDTO updated = uiMetadataApplicationService.updateUIMetadata(id, uiMetadataDTO);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error("更新UI元数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除UI元数据
     * 
     * @param id UI元数据ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUIMetadata(@PathVariable Long id) {
        try {
            uiMetadataApplicationService.deleteUIMetadata(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除UI元数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取UI元数据
     * 
     * @param id UI元数据ID
     * @return UI元数据
     */
    @GetMapping("/{id}")
    public ApiResponse<UIMetadataDTO> getUIMetadataById(@PathVariable Long id) {
        try {
            UIMetadataDTO uiMetadata = uiMetadataApplicationService.getUIMetadataById(id);
            return ApiResponse.success(uiMetadata);
        } catch (Exception e) {
            return ApiResponse.error("获取UI元数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户ID分页查询UI元数据
     * 
     * @param tenantId 租户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/by-tenant/{tenantId}/page")
    public ApiResponse<Page<UIMetadataDTO>> getUIMetadataByTenantIdPage(
            @PathVariable String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<UIMetadataDTO> result = uiMetadataApplicationService.getUIMetadataByTenantId(tenantId, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("查询UI元数据失败: " + e.getMessage());
        }
    }
} 