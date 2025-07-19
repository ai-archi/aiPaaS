package com.aixone.metacenter.dataservice.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.metacenter.dataservice.application.DataInstanceApplicationService;
import com.aixone.metacenter.dataservice.application.dto.DataInstanceDTO;
import com.aixone.metacenter.dataservice.application.dto.DataInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据实例控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@RestController
@RequestMapping("/api/v1/data-instances")
public class DataInstanceController {

    @Autowired
    private DataInstanceApplicationService dataInstanceApplicationService;

    /**
     * 创建数据实例
     * 
     * @param dataInstanceDTO 数据实例DTO
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<DataInstanceDTO> createDataInstance(@RequestBody DataInstanceDTO dataInstanceDTO) {
        try {
            DataInstanceDTO created = dataInstanceApplicationService.createDataInstance(dataInstanceDTO);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error("创建数据实例失败: " + e.getMessage());
        }
    }

    /**
     * 更新数据实例
     * 
     * @param id 数据实例ID
     * @param dataInstanceDTO 数据实例DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResponse<DataInstanceDTO> updateDataInstance(@PathVariable Long id, @RequestBody DataInstanceDTO dataInstanceDTO) {
        try {
            DataInstanceDTO updated = dataInstanceApplicationService.updateDataInstance(id, dataInstanceDTO);
            return ApiResponse.success(updated);
        } catch (Exception e) {
            return ApiResponse.error("更新数据实例失败: " + e.getMessage());
        }
    }

    /**
     * 删除数据实例
     * 
     * @param id 数据实例ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDataInstance(@PathVariable Long id) {
        try {
            dataInstanceApplicationService.deleteDataInstance(id);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除数据实例失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取数据实例
     * 
     * @param id 数据实例ID
     * @return 数据实例
     */
    @GetMapping("/{id}")
    public ApiResponse<DataInstanceDTO> getDataInstanceById(@PathVariable Long id) {
        try {
            DataInstanceDTO dataInstance = dataInstanceApplicationService.getDataInstanceById(id);
            return ApiResponse.success(dataInstance);
        } catch (Exception e) {
            return ApiResponse.error("获取数据实例失败: " + e.getMessage());
        }
    }

    /**
     * 根据元数据对象ID获取数据实例列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数据实例列表
     */
    @GetMapping("/by-meta-object/{metaObjectId}")
    public ApiResponse<List<DataInstanceDTO>> getDataInstancesByMetaObjectId(@PathVariable Long metaObjectId) {
        try {
            List<DataInstanceDTO> instances = dataInstanceApplicationService.getDataInstancesByMetaObjectId(metaObjectId);
            return ApiResponse.success(instances);
        } catch (Exception e) {
            return ApiResponse.error("获取数据实例列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户ID获取数据实例列表
     * 
     * @param tenantId 租户ID
     * @return 数据实例列表
     */
    @GetMapping("/by-tenant/{tenantId}")
    public ApiResponse<List<DataInstanceDTO>> getDataInstancesByTenantId(@PathVariable String tenantId) {
        try {
            List<DataInstanceDTO> instances = dataInstanceApplicationService.getDataInstancesByTenantId(tenantId);
            return ApiResponse.success(instances);
        } catch (Exception e) {
            return ApiResponse.error("获取数据实例列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据状态获取数据实例列表
     * 
     * @param status 状态
     * @return 数据实例列表
     */
    @GetMapping("/by-status/{status}")
    public ApiResponse<List<DataInstanceDTO>> getDataInstancesByStatus(@PathVariable String status) {
        try {
            List<DataInstanceDTO> instances = dataInstanceApplicationService.getDataInstancesByStatus(status);
            return ApiResponse.success(instances);
        } catch (Exception e) {
            return ApiResponse.error("获取数据实例列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询数据实例
     * 
     * @param query 查询条件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @PostMapping("/search")
    public ApiResponse<Page<DataInstanceDTO>> searchDataInstances(
            @RequestBody DataInstanceQuery query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<DataInstanceDTO> result = dataInstanceApplicationService.getDataInstances(query, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("查询数据实例失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建数据实例
     * 
     * @param dataInstanceDTOs 数据实例DTO列表
     * @return 创建结果
     */
    @PostMapping("/batch")
    public ApiResponse<List<DataInstanceDTO>> batchCreateDataInstances(@RequestBody List<DataInstanceDTO> dataInstanceDTOs) {
        try {
            List<DataInstanceDTO> created = dataInstanceApplicationService.batchCreateDataInstances(dataInstanceDTOs);
            return ApiResponse.success(created);
        } catch (Exception e) {
            return ApiResponse.error("批量创建数据实例失败: " + e.getMessage());
        }
    }
} 