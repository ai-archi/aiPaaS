package com.aixone.metacenter.metamanagement.application;

import com.aixone.metacenter.common.exception.MetaNotFoundException;
import com.aixone.metacenter.common.exception.MetaValidationException;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectDTO;
import com.aixone.metacenter.metamanagement.application.dto.MetaObjectQuery;
import com.aixone.metacenter.metamanagement.domain.MetaObject;
import com.aixone.metacenter.metamanagement.domain.MetaObjectRepository;
import com.aixone.metacenter.metamanagement.domain.service.MetaObjectDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 元数据对象应用服务
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetaObjectApplicationService {

    private final MetaObjectRepository metaObjectRepository;
    private final MetaObjectDomainService metaObjectDomainService;
    private final MetaObjectMapper metaObjectMapper;

    /**
     * 创建元数据对象
     * 
     * @param dto 元数据对象DTO
     * @return 创建的元数据对象DTO
     */
    @Transactional
    public MetaObjectDTO createMetaObject(MetaObjectDTO dto) {
        log.info("创建元数据对象: {}", dto.getName());

        // 参数校验
        validateMetaObject(dto);

        // 领域服务校验
        metaObjectDomainService.validateMetaObject(dto);

        // 检查唯一性
        if (metaObjectRepository.existsByTenantIdAndName(dto.getTenantId(), dto.getName())) {
            throw new MetaValidationException("元数据对象名称已存在: " + dto.getName());
        }

        // 构建聚合根
        MetaObject metaObject = metaObjectMapper.toEntity(dto);
        metaObject.setVersion(1);

        // 持久化
        MetaObject savedMetaObject = metaObjectRepository.save(metaObject);

        log.info("元数据对象创建成功: {}", savedMetaObject.getId());
        return metaObjectMapper.toDTO(savedMetaObject);
    }

    /**
     * 更新元数据对象
     * 
     * @param id 元数据对象ID
     * @param dto 元数据对象DTO
     * @return 更新后的元数据对象DTO
     */
    @Transactional
    public MetaObjectDTO updateMetaObject(Long id, MetaObjectDTO dto) {
        log.info("更新元数据对象: {}", id);

        // 加载聚合根
        MetaObject metaObject = metaObjectRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据对象不存在: " + id));

        // 参数校验
        validateMetaObject(dto);

        // 领域服务校验
        metaObjectDomainService.validateMetaObject(dto);

        // 检查名称唯一性（排除自身）
        if (!metaObject.getName().equals(dto.getName()) &&
                metaObjectRepository.existsByTenantIdAndName(dto.getTenantId(), dto.getName())) {
            throw new MetaValidationException("元数据对象名称已存在: " + dto.getName());
        }

        // 变更聚合根
        metaObjectMapper.updateEntityFromDTO(dto, metaObject);
        metaObject.incrementVersion();

        // 持久化
        MetaObject savedMetaObject = metaObjectRepository.save(metaObject);

        log.info("元数据对象更新成功: {}", savedMetaObject.getId());
        return metaObjectMapper.toDTO(savedMetaObject);
    }

    /**
     * 删除元数据对象
     * 
     * @param id 元数据对象ID
     */
    @Transactional
    public void deleteMetaObject(Long id) {
        log.info("删除元数据对象: {}", id);

        // 加载聚合根
        MetaObject metaObject = metaObjectRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据对象不存在: " + id));

        // 依赖分析
        metaObjectDomainService.analyzeDependencies(metaObject);

        // 软删除
        metaObject.setStatus("deleted");
        metaObjectRepository.save(metaObject);

        log.info("元数据对象删除成功: {}", id);
    }

    /**
     * 获取元数据对象
     * 
     * @param id 元数据对象ID
     * @return 元数据对象DTO
     */
    public MetaObjectDTO getMetaObject(Long id) {
        log.debug("获取元数据对象: {}", id);

        MetaObject metaObject = metaObjectRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("元数据对象不存在: " + id));

        return metaObjectMapper.toDTO(metaObject);
    }

    /**
     * 根据租户ID和名称获取元数据对象
     * 
     * @param tenantId 租户ID
     * @param name 名称
     * @return 元数据对象DTO
     */
    public MetaObjectDTO getMetaObjectByName(String tenantId, String name) {
        log.debug("根据名称获取元数据对象: tenantId={}, name={}", tenantId, name);

        MetaObject metaObject = metaObjectRepository.findByTenantIdAndName(tenantId, name)
                .orElseThrow(() -> new MetaNotFoundException("元数据对象不存在: tenantId=" + tenantId + ", name=" + name));

        return metaObjectMapper.toDTO(metaObject);
    }

    /**
     * 分页查询元数据对象
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    public Page<MetaObjectDTO> listMetaObjects(MetaObjectQuery query) {
        log.debug("分页查询元数据对象: {}", query);

        // 构建分页参数
        Sort sort = Sort.by(Sort.Direction.fromString(query.getSortDirection()), query.getSortBy());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);

        // 执行查询
        Page<MetaObject> page;
        if (StringUtils.hasText(query.getName())) {
            page = metaObjectRepository.findByTenantIdAndNameContainingIgnoreCase(
                    query.getTenantId(), query.getName(), pageable);
        } else if (StringUtils.hasText(query.getDescription())) {
            page = metaObjectRepository.findByTenantIdAndDescriptionContainingIgnoreCase(
                    query.getTenantId(), query.getDescription(), pageable);
        } else if (StringUtils.hasText(query.getTags())) {
            page = metaObjectRepository.findByTenantIdAndTagsContaining(
                    query.getTenantId(), query.getTags(), pageable);
        } else if (query.getTypes() != null && !query.getTypes().isEmpty()) {
            page = metaObjectRepository.findByTenantIdAndTypeIn(
                    query.getTenantId(), query.getTypes(), pageable);
        } else if (query.getLifecycles() != null && !query.getLifecycles().isEmpty()) {
            page = metaObjectRepository.findByTenantIdAndLifecycleIn(
                    query.getTenantId(), query.getLifecycles(), pageable);
        } else {
            page = metaObjectRepository.findByTenantId(query.getTenantId(), pageable);
        }

        return page.map(metaObjectMapper::toDTO);
    }

    /**
     * 根据类型查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param type 类型
     * @return 元数据对象列表
     */
    public List<MetaObjectDTO> listMetaObjectsByType(String tenantId, String type) {
        log.debug("根据类型查询元数据对象: tenantId={}, type={}", tenantId, type);

        List<MetaObject> metaObjects = metaObjectRepository.findByTenantIdAndType(tenantId, type);
        return metaObjects.stream().map(metaObjectMapper::toDTO).toList();
    }

    /**
     * 根据对象类型查询元数据对象列表
     * 
     * @param tenantId 租户ID
     * @param objectType 对象类型
     * @return 元数据对象列表
     */
    public List<MetaObjectDTO> listMetaObjectsByObjectType(String tenantId, String objectType) {
        log.debug("根据对象类型查询元数据对象: tenantId={}, objectType={}", tenantId, objectType);

        List<MetaObject> metaObjects = metaObjectRepository.findByTenantIdAndObjectType(tenantId, objectType);
        return metaObjects.stream().map(metaObjectMapper::toDTO).toList();
    }

    /**
     * 校验元数据对象
     * 
     * @param dto 元数据对象DTO
     * @return 校验结果
     */
    public boolean validateMetaObject(MetaObjectDTO dto) {
        log.debug("校验元数据对象: {}", dto.getName());

        // 基础参数校验
        if (!StringUtils.hasText(dto.getName())) {
            throw new MetaValidationException("元数据对象名称不能为空");
        }
        if (!StringUtils.hasText(dto.getType())) {
            throw new MetaValidationException("元数据对象类型不能为空");
        }
        if (!StringUtils.hasText(dto.getObjectType())) {
            throw new MetaValidationException("元数据对象类型不能为空");
        }
        if (!StringUtils.hasText(dto.getTenantId())) {
            throw new MetaValidationException("租户ID不能为空");
        }

        // 领域服务校验
        return metaObjectDomainService.validateMetaObject(dto);
    }

    /**
     * 预览元数据对象变更
     * 
     * @param dto 元数据对象DTO
     * @return 变更预览结果
     */
    public Object previewMetaObject(MetaObjectDTO dto) {
        log.debug("预览元数据对象变更: {}", dto.getName());

        return metaObjectDomainService.previewMetaObject(dto);
    }
} 