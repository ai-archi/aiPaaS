package com.aixone.metacenter.metamanagement.interfaces;

import com.aixone.metacenter.metamanagement.application.MetaRelationApplicationService;
import com.aixone.metacenter.metamanagement.application.dto.MetaRelationDTO;
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
 * 元数据关系控制器
 * 提供元数据关系的REST API接口
 */
@Slf4j
@RestController
@RequestMapping(MetaConstants.Api.API_PREFIX + "/meta-relations")
@RequiredArgsConstructor
public class MetaRelationController {

    private final MetaRelationApplicationService metaRelationApplicationService;

    /**
     * 创建元数据关系
     *
     * @param metaRelationDTO 元数据关系DTO
     * @return 创建的元数据关系
     */
    @PostMapping
    public ResponseEntity<MetaRelationDTO> createMetaRelation(@Valid @RequestBody MetaRelationDTO metaRelationDTO) {
        try {
            log.info("创建元数据关系: {}", metaRelationDTO.getName());
            MetaRelationDTO createdRelation = metaRelationApplicationService.createMetaRelation(metaRelationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRelation);
        } catch (Exception e) {
            log.error("创建元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_CREATE_ERROR", "创建元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 更新元数据关系
     *
     * @param id 关系ID
     * @param metaRelationDTO 元数据关系DTO
     * @return 更新后的元数据关系
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetaRelationDTO> updateMetaRelation(@PathVariable Long id, 
                                                             @Valid @RequestBody MetaRelationDTO metaRelationDTO) {
        try {
            log.info("更新元数据关系: {}", id);
            MetaRelationDTO updatedRelation = metaRelationApplicationService.updateMetaRelation(id, metaRelationDTO);
            return ResponseEntity.ok(updatedRelation);
        } catch (Exception e) {
            log.error("更新元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_UPDATE_ERROR", "更新元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 删除元数据关系
     *
     * @param id 关系ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetaRelation(@PathVariable Long id) {
        try {
            log.info("删除元数据关系: {}", id);
            metaRelationApplicationService.deleteMetaRelation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_DELETE_ERROR", "删除元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询元数据关系
     *
     * @param id 关系ID
     * @return 元数据关系
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetaRelationDTO> getMetaRelationById(@PathVariable Long id) {
        try {
            log.debug("查询元数据关系: {}", id);
            MetaRelationDTO metaRelation = metaRelationApplicationService.getMetaRelationById(id);
            return ResponseEntity.ok(metaRelation);
        } catch (Exception e) {
            log.error("查询元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_QUERY_ERROR", "查询元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 根据元数据对象ID查询关系列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 关系列表
     */
    @GetMapping("/by-object/{metaObjectId}")
    public ResponseEntity<List<MetaRelationDTO>> getMetaRelationsByMetaObjectId(@PathVariable Long metaObjectId) {
        try {
            log.debug("查询元数据对象的关系列表: {}", metaObjectId);
            List<MetaRelationDTO> relations = metaRelationApplicationService.getMetaRelationsByMetaObjectId(metaObjectId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            log.error("查询元数据对象关系列表失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_QUERY_ERROR", "查询元数据对象关系列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询元数据关系
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<MetaRelationDTO>> getMetaRelations(MetaObjectQuery query, Pageable pageable) {
        try {
            log.debug("分页查询元数据关系: {}", query);
            Page<MetaRelationDTO> relations = metaRelationApplicationService.getMetaRelations(query, pageable);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            log.error("分页查询元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_QUERY_ERROR", "分页查询元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 根据名称查询元数据关系
     *
     * @param name 关系名称
     * @return 元数据关系
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<MetaRelationDTO> getMetaRelationByName(@PathVariable String name) {
        try {
            log.debug("根据名称查询元数据关系: {}", name);
            Optional<MetaRelationDTO> metaRelation = metaRelationApplicationService.getMetaRelationByName(name);
            return metaRelation.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("根据名称查询元数据关系失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_QUERY_ERROR", "根据名称查询元数据关系失败: " + e.getMessage());
        }
    }

    /**
     * 检查关系名称是否存在
     *
     * @param name 关系名称
     * @param metaObjectId 元数据对象ID
     * @return 是否存在
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name, @RequestParam Long metaObjectId) {
        try {
            log.debug("检查关系名称是否存在: {}, {}", name, metaObjectId);
            boolean exists = metaRelationApplicationService.existsByName(name, metaObjectId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("检查关系名称是否存在失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_EXISTS_ERROR", "检查关系名称是否存在失败: " + e.getMessage());
        }
    }

    /**
     * 根据源对象ID查询关系列表
     *
     * @param sourceObjectId 源对象ID
     * @return 关系列表
     */
    @GetMapping("/by-source/{sourceObjectId}")
    public ResponseEntity<List<MetaRelationDTO>> getMetaRelationsBySourceObjectId(@PathVariable Long sourceObjectId) {
        try {
            log.debug("根据源对象ID查询关系列表: {}", sourceObjectId);
            List<MetaRelationDTO> relations = metaRelationApplicationService.getMetaRelationsBySourceObjectId(sourceObjectId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            log.error("根据源对象ID查询关系列表失败: {}", e.getMessage(), e);
            throw new MetaException("META_RELATION_QUERY_ERROR", "根据源对象ID查询关系列表失败: " + e.getMessage());
        }
    }
} 