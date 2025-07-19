package com.aixone.metacenter.metamanagement.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.metacenter.metamanagement.application.MetaObjectApplicationService;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 元数据对象REST控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/meta-objects")
@RequiredArgsConstructor
public class MetaObjectController {

    private final MetaObjectApplicationService metaObjectApplicationService;

    /**
     * 创建元数据对象
     * 
     * @param metaObjectDTO 元数据对象DTO
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MetaObjectDTO>> createMetaObject(@Valid @RequestBody MetaObjectDTO metaObjectDTO) {
        log.info("创建元数据对象: {}", metaObjectDTO.getName());
        MetaObjectDTO created = metaObjectApplicationService.createMetaObject(metaObjectDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "元数据对象创建成功"));
    }

    /**
     * 根据ID查询元数据对象
     * 
     * @param id 元数据对象ID
     * @return 元数据对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> getMetaObjectById(@PathVariable Long id) {
        log.info("根据ID查询元数据对象: {}", id);
        MetaObjectDTO metaObject = metaObjectApplicationService.getMetaObjectById(id);
        return ResponseEntity.ok(ApiResponse.success(metaObject, "查询成功"));
    }

    /**
     * 根据租户ID和名称查询元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 元数据对象
     */
    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> getMetaObjectByName(
            @RequestParam String tenantId, 
            @RequestParam String name) {
        log.info("根据租户ID和名称查询元数据对象: tenantId={}, name={}", tenantId, name);
        MetaObjectDTO metaObject = metaObjectApplicationService.getMetaObjectByName(tenantId, name);
        return ResponseEntity.ok(ApiResponse.success(metaObject, "查询成功"));
    }

    /**
     * 分页查询元数据对象
     * 
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MetaObjectDTO>>> getMetaObjects(
            MetaObjectQuery query, 
            Pageable pageable) {
        log.info("分页查询元数据对象: query={}, pageable={}", query, pageable);
        Page<MetaObjectDTO> result = metaObjectApplicationService.getMetaObjects(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "查询成功"));
    }

    /**
     * 根据租户ID查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @return 元数据对象列表
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MetaObjectDTO>>> getMetaObjectsByTenantId(@PathVariable String tenantId) {
        log.info("根据租户ID查询元数据对象列表: {}", tenantId);
        List<MetaObjectDTO> metaObjects = metaObjectApplicationService.getMetaObjectsByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(metaObjects, "查询成功"));
    }

    /**
     * 根据租户ID和对象类型查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 元数据对象列表
     */
    @GetMapping("/tenant/{tenantId}/type/{objectType}")
    public ResponseEntity<ApiResponse<List<MetaObjectDTO>>> getMetaObjectsByTenantIdAndType(
            @PathVariable String tenantId, 
            @PathVariable String objectType) {
        log.info("根据租户ID和对象类型查询元数据对象列表: tenantId={}, objectType={}", tenantId, objectType);
        List<MetaObjectDTO> metaObjects = metaObjectApplicationService.getMetaObjectsByTenantIdAndType(tenantId, objectType);
        return ResponseEntity.ok(ApiResponse.success(metaObjects, "查询成功"));
    }

    /**
     * 更新元数据对象
     * 
     * @param id 元数据对象ID
     * @param metaObjectDTO 元数据对象DTO
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> updateMetaObject(
            @PathVariable Long id, 
            @Valid @RequestBody MetaObjectDTO metaObjectDTO) {
        log.info("更新元数据对象: id={}, name={}", id, metaObjectDTO.getName());
        MetaObjectDTO updated = metaObjectApplicationService.updateMetaObject(id, metaObjectDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "元数据对象更新成功"));
    }

    /**
     * 删除元数据对象
     * 
     * @param id 元数据对象ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMetaObject(@PathVariable Long id) {
        log.info("删除元数据对象: {}", id);
        metaObjectApplicationService.deleteMetaObject(id);
        return ResponseEntity.ok(ApiResponse.success(null, "元数据对象删除成功"));
    }

    /**
     * 根据租户ID删除元数据对象
     * 
     * @param tenantId 租户ID
     * @return 删除结果
     */
    @DeleteMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteMetaObjectsByTenantId(@PathVariable String tenantId) {
        log.info("根据租户ID删除元数据对象: {}", tenantId);
        metaObjectApplicationService.deleteMetaObjectsByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "元数据对象删除成功"));
    }

    /**
     * 检查元数据对象名称是否存在
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 检查结果
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> existsByName(
            @RequestParam String tenantId, 
            @RequestParam String name) {
        log.info("检查元数据对象名称是否存在: tenantId={}, name={}", tenantId, name);
        boolean exists = metaObjectApplicationService.existsByName(tenantId, name);
        return ResponseEntity.ok(ApiResponse.success(exists, "检查完成"));
    }

    /**
     * 统计元数据对象数量
     * 
     * @param tenantId 租户ID
     * @return 统计结果
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countByTenantId(@RequestParam String tenantId) {
        log.info("统计元数据对象数量: {}", tenantId);
        long count = metaObjectApplicationService.countByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.success(count, "统计完成"));
    }

    /**
     * 发布元数据对象
     * 
     * @param id 元数据对象ID
     * @return 发布结果
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> publishMetaObject(@PathVariable Long id) {
        log.info("发布元数据对象: {}", id);
        MetaObjectDTO published = metaObjectApplicationService.publishMetaObject(id);
        return ResponseEntity.ok(ApiResponse.success(published, "元数据对象发布成功"));
    }

    /**
     * 废弃元数据对象
     * 
     * @param id 元数据对象ID
     * @return 废弃结果
     */
    @PostMapping("/{id}/deprecate")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> deprecateMetaObject(@PathVariable Long id) {
        log.info("废弃元数据对象: {}", id);
        MetaObjectDTO deprecated = metaObjectApplicationService.deprecateMetaObject(id);
        return ResponseEntity.ok(ApiResponse.success(deprecated, "元数据对象废弃成功"));
    }

    /**
     * 归档元数据对象
     * 
     * @param id 元数据对象ID
     * @return 归档结果
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<MetaObjectDTO>> archiveMetaObject(@PathVariable Long id) {
        log.info("归档元数据对象: {}", id);
        MetaObjectDTO archived = metaObjectApplicationService.archiveMetaObject(id);
        return ResponseEntity.ok(ApiResponse.success(archived, "元数据对象归档成功"));
    }
} 