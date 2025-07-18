package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.metamanagement.application.dto.MetaAttributeDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import com.aixone.metacenter.metamanagement.domain.MetaAttribute;
import com.aixone.metacenter.metamanagement.domain.MetaAttributeRepository;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import com.aixone.metacenter.metamanagement.domain.service.MetaAttributeDomainService;
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
 * 元数据属性应用服务
 * 负责元数据属性的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MetaAttributeApplicationService {

    private final MetaAttributeRepository metaAttributeRepository;
    private final MetaObjectRepository metaObjectRepository;
    private final MetaAttributeDomainService metaAttributeDomainService;
    private final MetaAttributeMapper metaAttributeMapper;

    /**
     * 创建元数据属性
     *
     * @param metaAttributeDTO 元数据属性DTO
     * @return 创建的元数据属性DTO
     */
    public MetaAttributeDTO createMetaAttribute(MetaAttributeDTO metaAttributeDTO) {
        log.info("创建元数据属性: {}", metaAttributeDTO.getName());
        
        // 验证元数据对象是否存在
        MetaObject metaObject = metaObjectRepository.findById(metaAttributeDTO.getMetaObjectId())
                .orElseThrow(() -> new MetaNotFoundException("元数据对象不存在: " + metaAttributeDTO.getMetaObjectId()));
        
        // 验证属性名称唯一性
        if (metaAttributeRepository.existsByNameAndMetaObjectId(metaAttributeDTO.getName(), metaAttributeDTO.getMetaObjectId())) {
            throw new MetaValidationException("属性名称已存在: " + metaAttributeDTO.getName());
        }
        
        // 转换为领域实体
        MetaAttribute metaAttribute = metaAttributeMapper.toEntity(metaAttributeDTO);
        metaAttribute.setMetaObject(metaObject);
        metaAttribute.setCreatedTime(LocalDateTime.now());
        metaAttribute.setUpdatedTime(LocalDateTime.now());
        
        // 领域服务验证
        metaAttributeDomainService.validateMetaAttribute(metaAttribute);
        
        // 保存实体
        MetaAttribute savedAttribute = metaAttributeRepository.save(metaAttribute);
        
        log.info("元数据属性创建成功: {}", savedAttribute.getId());
        return metaAttributeMapper.toDTO(savedAttribute);
    }

    /**
     * 更新元数据属性
     *
     * @param id 属性ID
     * @param metaAttributeDTO 元数据属性DTO
     * @return 更新后的元数据属性DTO
     */
    public MetaAttributeDTO updateMetaAttribute(Long id, MetaAttributeDTO metaAttributeDTO) {
        log.info("更新元数据属性: {}", id);
        
        MetaAttribute existingAttribute = metaAttributeRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据属性不存在: " + id));
        
        // 验证属性名称唯一性（排除自身）
        if (!existingAttribute.getName().equals(metaAttributeDTO.getName()) &&
            metaAttributeRepository.existsByNameAndMetaObjectId(metaAttributeDTO.getName(), existingAttribute.getMetaObject().getId())) {
            throw new MetaValidationException("属性名称已存在: " + metaAttributeDTO.getName());
        }
        
        // 更新属性
        existingAttribute.setName(metaAttributeDTO.getName());
        existingAttribute.setDisplayName(metaAttributeDTO.getDisplayName());
        existingAttribute.setDataType(metaAttributeDTO.getDataType());
        existingAttribute.setLength(metaAttributeDTO.getLength());
        existingAttribute.setPrecision(metaAttributeDTO.getPrecision());
        existingAttribute.setScale(metaAttributeDTO.getScale());
        existingAttribute.setRequired(metaAttributeDTO.getRequired());
        existingAttribute.setDefaultValue(metaAttributeDTO.getDefaultValue());
        existingAttribute.setDescription(metaAttributeDTO.getDescription());
        existingAttribute.setUpdatedTime(LocalDateTime.now());
        
        // 领域服务验证
        metaAttributeDomainService.validateMetaAttribute(existingAttribute);
        
        // 保存实体
        MetaAttribute savedAttribute = metaAttributeRepository.save(existingAttribute);
        
        log.info("元数据属性更新成功: {}", id);
        return metaAttributeMapper.toDTO(savedAttribute);
    }

    /**
     * 删除元数据属性
     *
     * @param id 属性ID
     */
    public void deleteMetaAttribute(Long id) {
        log.info("删除元数据属性: {}", id);
        
        MetaAttribute metaAttribute = metaAttributeRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据属性不存在: " + id));
        
        // 领域服务验证删除条件
        metaAttributeDomainService.validateDeleteMetaAttribute(metaAttribute);
        
        metaAttributeRepository.delete(metaAttribute);
        log.info("元数据属性删除成功: {}", id);
    }

    /**
     * 根据ID查询元数据属性
     *
     * @param id 属性ID
     * @return 元数据属性DTO
     */
    @Transactional(readOnly = true)
    public MetaAttributeDTO getMetaAttributeById(Long id) {
        log.debug("查询元数据属性: {}", id);
        
        MetaAttribute metaAttribute = metaAttributeRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据属性不存在: " + id));
        
        return metaAttributeMapper.toDTO(metaAttribute);
    }

    /**
     * 根据元数据对象ID查询属性列表
     *
     * @param metaObjectId 元数据对象ID
     * @return 属性列表
     */
    @Transactional(readOnly = true)
    public List<MetaAttributeDTO> getMetaAttributesByMetaObjectId(Long metaObjectId) {
        log.debug("查询元数据对象的属性列表: {}", metaObjectId);
        
        List<MetaAttribute> attributes = metaAttributeRepository.findByMetaObjectId(metaObjectId);
        return attributes.stream()
                .map(metaAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询元数据属性
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<MetaAttributeDTO> getMetaAttributes(MetaObjectQuery query, Pageable pageable) {
        log.debug("分页查询元数据属性: {}", query);
        
        Page<MetaAttribute> attributes = metaAttributeRepository.findByConditions(query, pageable);
        return attributes.map(metaAttributeMapper::toDTO);
    }

    /**
     * 根据名称查询元数据属性
     *
     * @param name 属性名称
     * @return 元数据属性DTO
     */
    @Transactional(readOnly = true)
    public Optional<MetaAttributeDTO> getMetaAttributeByName(String name) {
        log.debug("根据名称查询元数据属性: {}", name);
        
        return metaAttributeRepository.findByName(name)
                .map(metaAttributeMapper::toDTO);
    }

    /**
     * 检查属性名称是否存在
     *
     * @param name 属性名称
     * @param metaObjectId 元数据对象ID
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name, Long metaObjectId) {
        return metaAttributeRepository.existsByNameAndMetaObjectId(name, metaObjectId);
    }
} 