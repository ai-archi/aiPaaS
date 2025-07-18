package com.aixone.metacenter.metamanagement.interfaces;

import com.aixone.metacenter.metamanagement.application.MetaObjectApplicationService;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 元数据对象控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/meta-objects")
@RequiredArgsConstructor
@Tag(name = "元数据对象管理", description = "元数据对象的增删改查接口")
public class MetaObjectController {

    private final MetaObjectApplicationService metaObjectApplicationService;

    /**
     * 创建元数据对象
     * 
     * @param dto 元数据对象DTO
     * @return 创建的元数据对象
     */
    @PostMapping
    @Operation(summary = "创建元数据对象", description = "创建新的元数据对象")
    public ResponseEntity<MetaObjectDTO> createMetaObject(
            @Parameter(description = "元数据对象信息") @Valid @RequestBody MetaObjectDTO dto) {
        log.info("创建元数据对象请求: {}", dto.getName());
        
        MetaObjectDTO result = metaObjectApplicationService.createMetaObject(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新元数据对象
     * 
     * @param id 元数据对象ID
     * @param dto 元数据对象DTO
     * @return 更新后的元数据对象
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新元数据对象", description = "更新指定ID的元数据对象")
    public ResponseEntity<MetaObjectDTO> updateMetaObject(
            @Parameter(description = "元数据对象ID") @PathVariable Long id,
            @Parameter(description = "元数据对象信息") @Valid @RequestBody MetaObjectDTO dto) {
        log.info("更新元数据对象请求: id={}", id);
        
        MetaObjectDTO result = metaObjectApplicationService.updateMetaObject(id, dto);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 删除元数据对象
     * 
     * @param id 元数据对象ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除元数据对象", description = "删除指定ID的元数据对象")
    public ResponseEntity<Void> deleteMetaObject(
            @Parameter(description = "元数据对象ID") @PathVariable Long id) {
        log.info("删除元数据对象请求: id={}", id);
        
        metaObjectApplicationService.deleteMetaObject(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取元数据对象
     * 
     * @param id 元数据对象ID
     * @return 元数据对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取元数据对象", description = "根据ID获取元数据对象详情")
    public ResponseEntity<MetaObjectDTO> getMetaObject(
            @Parameter(description = "元数据对象ID") @PathVariable Long id) {
        log.debug("获取元数据对象请求: id={}", id);
        
        MetaObjectDTO result = metaObjectApplicationService.getMetaObject(id);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据名称获取元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 元数据对象
     */
    @GetMapping("/by-name")
    @Operation(summary = "根据名称获取元数据对象", description = "根据租户ID和名称获取元数据对象")
    public ResponseEntity<MetaObjectDTO> getMetaObjectByName(
            @Parameter(description = "租户ID") @RequestParam String tenantId,
            @Parameter(description = "元数据对象名称") @RequestParam String name) {
        log.debug("根据名称获取元数据对象请求: tenantId={}, name={}", tenantId, name);
        
        MetaObjectDTO result = metaObjectApplicationService.getMetaObjectByName(tenantId, name);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询元数据对象
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询元数据对象", description = "根据条件分页查询元数据对象")
    public ResponseEntity<Page<MetaObjectDTO>> listMetaObjects(
            @Parameter(description = "查询条件") MetaObjectQuery query) {
        log.debug("分页查询元数据对象请求: {}", query);
        
        Page<MetaObjectDTO> result = metaObjectApplicationService.listMetaObjects(query);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据类型查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 元数据对象列表
     */
    @GetMapping("/by-type")
    @Operation(summary = "根据类型查询元数据对象", description = "根据租户ID和类型查询元数据对象列表")
    public ResponseEntity<List<MetaObjectDTO>> listMetaObjectsByType(
            @Parameter(description = "租户ID") @RequestParam String tenantId,
            @Parameter(description = "元数据类型") @RequestParam String type) {
        log.debug("根据类型查询元数据对象请求: tenantId={}, type={}", tenantId, type);
        
        List<MetaObjectDTO> result = metaObjectApplicationService.listMetaObjectsByType(tenantId, type);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据对象类型查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 元数据对象列表
     */
    @GetMapping("/by-object-type")
    @Operation(summary = "根据对象类型查询元数据对象", description = "根据租户ID和对象类型查询元数据对象列表")
    public ResponseEntity<List<MetaObjectDTO>> listMetaObjectsByObjectType(
            @Parameter(description = "租户ID") @RequestParam String tenantId,
            @Parameter(description = "对象类型") @RequestParam String objectType) {
        log.debug("根据对象类型查询元数据对象请求: tenantId={}, objectType={}", tenantId, objectType);
        
        List<MetaObjectDTO> result = metaObjectApplicationService.listMetaObjectsByObjectType(tenantId, objectType);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 校验元数据对象
     * 
     * @param dto 元数据对象DTO
     * @return 校验结果
     */
    @PostMapping("/validate")
    @Operation(summary = "校验元数据对象", description = "校验元数据对象的完整性和正确性")
    public ResponseEntity<Boolean> validateMetaObject(
            @Parameter(description = "元数据对象信息") @Valid @RequestBody MetaObjectDTO dto) {
        log.debug("校验元数据对象请求: {}", dto.getName());
        
        boolean result = metaObjectApplicationService.validateMetaObject(dto);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 预览元数据对象变更
     * 
     * @param dto 元数据对象DTO
     * @return 变更预览结果
     */
    @PostMapping("/preview")
    @Operation(summary = "预览元数据对象变更", description = "预览元数据对象变更的影响和风险")
    public ResponseEntity<Object> previewMetaObject(
            @Parameter(description = "元数据对象信息") @Valid @RequestBody MetaObjectDTO dto) {
        log.debug("预览元数据对象变更请求: {}", dto.getName());
        
        Object result = metaObjectApplicationService.previewMetaObject(dto);
        
        return ResponseEntity.ok(result);
    }
} 