package com.aixone.metacenter.metamanagement.interfaces;

import com.aixone.metacenter.metamanagement.application.MetaAttributeApplicationService;
import com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import com.aixone.metacenter.common.constant.MetaConstants;
import com.aixone.metacenter.common.exception.MetaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 元数据属性控制器
 * 提供元数据属性的REST API接口
 */
@Slf4j
@RestController
@RequestMapping(MetaConstants.Api.API_PREFIX + "/meta-attributes")
@RequiredArgsConstructor
public class MetaAttributeController {

    private final MetaAttributeApplicationService metaAttributeApplicationService;

    /**
     * 创建元数据属性
     *
     * @param metaAttributeDTO 元数据属性DTO
     * @return 创建的元数据属性
     */
    @PostMapping
    public ResponseEntity<MetaAttributeDTO> createMetaAttribute(@Valid @RequestBody MetaAttributeDTO metaAttributeDTO) {
        try {
            log.info("创建元数据属性: {}", metaAttributeDTO.getName());
            MetaAttributeDTO createdAttribute = metaAttributeApplicationService.createMetaAttribute(metaAttributeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAttribute);
        } catch (Exception e) {
            log.error("创建元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_CREATE_ERROR", "创建元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 更新元数据属性
     *
     * @param id 属性ID
     * @param metaAttributeDTO 元数据属性DTO
     * @return 更新后的元数据属性
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetaAttributeDTO> updateMetaAttribute(@PathVariable Long id, 
                                                               @Valid @RequestBody MetaAttributeDTO metaAttributeDTO) {
        try {
            log.info("更新元数据属性: {}", id);
            MetaAttributeDTO updatedAttribute = metaAttributeApplicationService.updateMetaAttribute(id, metaAttributeDTO);
            return ResponseEntity.ok(updatedAttribute);
        } catch (Exception e) {
            log.error("更新元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_UPDATE_ERROR", "更新元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 删除元数据属性
     *
     * @param id 属性ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetaAttribute(@PathVariable Long id) {
        try {
            log.info("删除元数据属性: {}", id);
            metaAttributeApplicationService.deleteMetaAttribute(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_DELETE_ERROR", "删除元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询元数据属性
     *
     * @param id 属性ID
     * @return 元数据属性
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetaAttributeDTO> getMetaAttributeById(@PathVariable Long id) {
        try {
            log.debug("查询元数据属性: {}", id);
            MetaAttributeDTO metaAttribute = metaAttributeApplicationService.getMetaAttributeById(id);
            return ResponseEntity.ok(metaAttribute);
        } catch (Exception e) {
            log.error("查询元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_QUERY_ERROR", "查询元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 根据元数据对象ID查询属性列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 属性列表
     */
    @GetMapping("/by-object/{metaObjectId}")
    public ResponseEntity<List<MetaAttributeDTO>> getMetaAttributesByMetaObjectId(@PathVariable Long metaObjectId) {
        try {
            log.debug("查询元数据对象的属性列表: {}", metaObjectId);
            List<MetaAttributeDTO> attributes = metaAttributeApplicationService.getMetaAttributesByMetaObjectId(metaObjectId);
            return ResponseEntity.ok(attributes);
        } catch (Exception e) {
            log.error("查询元数据对象属性列表失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_QUERY_ERROR", "查询元数据对象属性列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询元数据属性
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<MetaAttributeDTO>> getMetaAttributes(MetaObjectQuery query, Pageable pageable) {
        try {
            log.debug("分页查询元数据属性: {}", query);
            Page<MetaAttributeDTO> attributes = metaAttributeApplicationService.getMetaAttributes(query, pageable);
            return ResponseEntity.ok(attributes);
        } catch (Exception e) {
            log.error("分页查询元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_QUERY_ERROR", "分页查询元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 根据名称查询元数据属性
     *
     * @param name 属性名称
     * @return 元数据属性
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<MetaAttributeDTO> getMetaAttributeByName(@PathVariable String name) {
        try {
            log.debug("根据名称查询元数据属性: {}", name);
            Optional<MetaAttributeDTO> metaAttribute = metaAttributeApplicationService.getMetaAttributeByName(name);
            return metaAttribute.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("根据名称查询元数据属性失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_QUERY_ERROR", "根据名称查询元数据属性失败: " + e.getMessage());
        }
    }

    /**
     * 检查属性名称是否存在
     *
     * @param name 属性名称
     * @param metaObjectId 元数据对象ID
     * @return 是否存在
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name, @RequestParam Long metaObjectId) {
        try {
            log.debug("检查属性名称是否存在: {}, {}", name, metaObjectId);
            boolean exists = metaAttributeApplicationService.existsByName(name, metaObjectId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("检查属性名称是否存在失败: {}", e.getMessage(), e);
            throw new MetaException("META_ATTRIBUTE_EXISTS_ERROR", "检查属性名称是否存在失败: " + e.getMessage());
        }
    }
} 