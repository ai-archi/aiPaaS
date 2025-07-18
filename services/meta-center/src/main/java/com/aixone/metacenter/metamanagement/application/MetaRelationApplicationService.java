package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaRelationDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import com.aixone.metacenter.metamanagement.domain.MetaRelation;
import com.aixone.metacenter.metamanagement.domain.MetaRelationRepository;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import com.aixone.metacenter.metamanagement.domain.service.MetaRelationDomainService;
import com.aixone.metacenter.common.exception.MetaNotFoundException;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 元数据关系应用服务
 * 负责元数据关系的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MetaRelationApplicationService {

    private final MetaRelationRepository metaRelationRepository;
    private final MetaObjectRepository metaObjectRepository;
    private final MetaRelationDomainService metaRelationDomainService;
    private final MetaRelationMapper metaRelationMapper;

    /**
     * 创建元数据关系
     *
     * @param metaRelationDTO 元数据关系DTO
     * @return 创建的元数据关系DTO
     */
    public MetaRelationDTO createMetaRelation(MetaRelationDTO metaRelationDTO) {
        log.info("创建元数据关系: {}", metaRelationDTO.getName());
        
        // 验证源元数据对象是否存在
        MetaObject sourceObject = metaObjectRepository.findById(metaRelationDTO.getSourceObjectId())
                .orElseThrow(() -> new MetaNotFoundException("源元数据对象不存在: " + metaRelationDTO.getSourceObjectId()));
        
        // 验证目标元数据对象是否存在
        MetaObject targetObject = metaObjectRepository.findById(metaRelationDTO.getTargetObjectId())
                .orElseThrow(() -> new MetaNotFoundException("目标元数据对象不存在: " + metaRelationDTO.getTargetObjectId()));
        
        // 验证关系名称唯一性
        if (metaRelationRepository.existsByNameAndSourceObjectId(metaRelationDTO.getName(), metaRelationDTO.getSourceObjectId())) {
            throw new MetaValidationException("关系名称已存在: " + metaRelationDTO.getName());
        }
        
        // 转换为领域实体
        MetaRelation metaRelation = metaRelationMapper.toEntity(metaRelationDTO);
        metaRelation.setSourceObject(sourceObject);
        metaRelation.setTargetObject(targetObject);
        // 设置时间
        metaRelation.setCreatedAt(LocalDateTime.now());
        metaRelation.setUpdatedAt(LocalDateTime.now());
        
        // 领域服务验证
        metaRelationDomainService.validateMetaRelation(metaRelation);
        
        // 保存实体
        MetaRelation savedRelation = metaRelationRepository.save(metaRelation);
        
        log.info("元数据关系创建成功: {}", savedRelation.getId());
        return metaRelationMapper.toDTO(savedRelation);
    }

    /**
     * 更新元数据关系
     *
     * @param id 关系ID
     * @param metaRelationDTO 元数据关系DTO
     * @return 更新后的元数据关系DTO
     */
    public MetaRelationDTO updateMetaRelation(Long id, MetaRelationDTO metaRelationDTO) {
        log.info("更新元数据关系: {}", id);
        
        MetaRelation existingRelation = metaRelationRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据关系不存在: " + id));
        
        // 验证关系名称唯一性（排除自身）
        if (!existingRelation.getName().equals(metaRelationDTO.getName()) &&
            metaRelationRepository.existsByNameAndSourceObjectId(metaRelationDTO.getName(), existingRelation.getSourceObject().getId())) {
            throw new MetaValidationException("关系名称已存在: " + metaRelationDTO.getName());
        }
        
        // 更新关系
        existingRelation.setName(metaRelationDTO.getName());
        existingRelation.setDisplayName(metaRelationDTO.getDisplayName());
        existingRelation.setRelationType(metaRelationDTO.getRelationType());
        existingRelation.setCardinality(metaRelationDTO.getCardinality());
        existingRelation.setDescription(metaRelationDTO.getDescription());
        existingRelation.setUpdatedAt(LocalDateTime.now());
        
        // 领域服务验证
        metaRelationDomainService.validateMetaRelation(existingRelation);
        
        // 保存实体
        MetaRelation savedRelation = metaRelationRepository.save(existingRelation);
        
        log.info("元数据关系更新成功: {}", id);
        return metaRelationMapper.toDTO(savedRelation);
    }

    /**
     * 删除元数据关系
     *
     * @param id 关系ID
     */
    public void deleteMetaRelation(Long id) {
        log.info("删除元数据关系: {}", id);
        
        MetaRelation metaRelation = metaRelationRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据关系不存在: " + id));
        
        // 领域服务验证删除条件
        metaRelationDomainService.validateDeleteMetaRelation(metaRelation);
        
        metaRelationRepository.delete(metaRelation);
        log.info("元数据关系删除成功: {}", id);
    }

    /**
     * 根据ID查询元数据关系
     *
     * @param id 关系ID
     * @return 元数据关系DTO
     */
    @Transactional(readOnly = true)
    public MetaRelationDTO getMetaRelationById(Long id) {
        log.debug("查询元数据关系: {}", id);
        
        MetaRelation metaRelation = metaRelationRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据关系不存在: " + id));
        
        return metaRelationMapper.toDTO(metaRelation);
    }

    /**
     * 根据源对象ID查询关系列表
     *
     * @param sourceObjectId 源对象ID
     * @return 关系列表
     */
    @Transactional(readOnly = true)
    public List<MetaRelationDTO> getMetaRelationsBySourceObjectId(Long sourceObjectId) {
        log.debug("查询源对象的关系列表: {}", sourceObjectId);
        
        List<MetaRelation> relations = metaRelationRepository.findBySourceObjectId(sourceObjectId);
        return relations.stream()
                .map(metaRelationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据目标对象ID查询关系列表
     *
     * @param targetObjectId 目标对象ID
     * @return 关系列表
     */
    @Transactional(readOnly = true)
    public List<MetaRelationDTO> getMetaRelationsByTargetObjectId(Long targetObjectId) {
        log.debug("查询目标对象的关系列表: {}", targetObjectId);
        
        List<MetaRelation> relations = metaRelationRepository.findByTargetObjectId(targetObjectId);
        return relations.stream()
                .map(metaRelationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询元数据关系
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<MetaRelationDTO> getMetaRelations(MetaObjectQuery query, Pageable pageable) {
        log.debug("分页查询元数据关系: {}", query);
        
        Page<MetaRelation> relations = metaRelationRepository.findByConditions(query, pageable);
        return relations.map(metaRelationMapper::toDTO);
    }

    /**
     * 根据名称查询元数据关系
     *
     * @param name 关系名称
     * @return 元数据关系DTO
     */
    @Transactional(readOnly = true)
    public Optional<MetaRelationDTO> getMetaRelationByName(String name) {
        log.debug("根据名称查询元数据关系: {}", name);
        
        return metaRelationRepository.findByName(name)
                .map(metaRelationMapper::toDTO);
    }

    /**
     * 检查关系名称是否存在
     *
     * @param name 关系名称
     * @param sourceObjectId 源对象ID
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name, Long sourceObjectId) {
        return metaRelationRepository.existsByNameAndSourceObjectId(name, sourceObjectId);
    }

    /**
     * 根据关系类型查询关系列表
     *
     * @param relationType 关系类型
     * @return 关系列表
     */
    @Transactional(readOnly = true)
    public List<MetaRelationDTO> getMetaRelationsByType(String relationType) {
        log.debug("根据关系类型查询: {}", relationType);
        
        List<MetaRelation> relations = metaRelationRepository.findByRelationType(relationType);
        return relations.stream()
                .map(metaRelationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据元数据对象ID获取关系列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 关系列表
     */
    public List<MetaRelationDTO> getMetaRelationsByMetaObjectId(Long metaObjectId) {
        List<MetaRelation> relations = metaRelationRepository.findBySourceMetaObjectIdOrTargetMetaObjectId(metaObjectId, metaObjectId);
        return relations.stream()
                .map(metaRelationMapper::toDTO)
                .collect(Collectors.toList());
    }
} 