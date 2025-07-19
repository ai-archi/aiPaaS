package com.aixone.metacenter.dataservice.application;

import com.aixone.metacenter.dataservice.application.dto.DataInstanceDTO;
import com.aixone.metacenter.dataservice.application.dto.DataInstanceQuery;
import com.aixone.metacenter.dataservice.domain.DataInstance;
import com.aixone.metacenter.dataservice.domain.DataInstanceRepository;
import com.aixone.metacenter.dataservice.application.DataInstanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据实例应用服务
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Service
@Transactional
public class DataInstanceApplicationService {

    @Autowired
    private DataInstanceRepository dataInstanceRepository;

    @Autowired
    private DataInstanceMapper dataInstanceMapper;

    /**
     * 创建数据实例
     * 
     * @param dataInstanceDTO 数据实例DTO
     * @return 创建的数据实例DTO
     */
    public DataInstanceDTO createDataInstance(DataInstanceDTO dataInstanceDTO) {
        DataInstance dataInstance = dataInstanceMapper.toEntity(dataInstanceDTO);
        
        // 设置创建时间
        dataInstance.setCreatedAt(LocalDateTime.now());
        dataInstance.setUpdatedAt(LocalDateTime.now());
        
        DataInstance savedInstance = dataInstanceRepository.save(dataInstance);
        return dataInstanceMapper.toDTO(savedInstance);
    }

    /**
     * 更新数据实例
     * 
     * @param id 数据实例ID
     * @param dataInstanceDTO 数据实例DTO
     * @return 更新后的数据实例DTO
     */
    public DataInstanceDTO updateDataInstance(Long id, DataInstanceDTO dataInstanceDTO) {
        Optional<DataInstance> optional = dataInstanceRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("数据实例不存在: " + id);
        }
        
        DataInstance existingInstance = optional.get();
        dataInstanceMapper.updateEntityFromDTO(dataInstanceDTO, existingInstance);
        existingInstance.setUpdatedAt(LocalDateTime.now());
        
        DataInstance updatedInstance = dataInstanceRepository.save(existingInstance);
        return dataInstanceMapper.toDTO(updatedInstance);
    }

    /**
     * 删除数据实例
     * 
     * @param id 数据实例ID
     */
    public void deleteDataInstance(Long id) {
        if (!dataInstanceRepository.existsById(id)) {
            throw new RuntimeException("数据实例不存在: " + id);
        }
        dataInstanceRepository.deleteById(id);
    }

    /**
     * 根据ID获取数据实例
     * 
     * @param id 数据实例ID
     * @return 数据实例DTO
     */
    @Transactional(readOnly = true)
    public DataInstanceDTO getDataInstanceById(Long id) {
        Optional<DataInstance> optional = dataInstanceRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("数据实例不存在: " + id);
        }
        return dataInstanceMapper.toDTO(optional.get());
    }

    /**
     * 根据元数据对象ID获取数据实例列表
     * 
     * @param metaObjectId 元数据对象ID
     * @return 数据实例列表
     */
    @Transactional(readOnly = true)
    public List<DataInstanceDTO> getDataInstancesByMetaObjectId(Long metaObjectId) {
        List<DataInstance> instances = dataInstanceRepository.findByMetaObjectId(metaObjectId);
        return instances.stream()
                .map(dataInstanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据查询条件分页查询数据实例
     * 
     * @param query 查询条件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<DataInstanceDTO> getDataInstances(DataInstanceQuery query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DataInstance> instances = dataInstanceRepository.findByQuery(query, pageable);
        return instances.map(dataInstanceMapper::toDTO);
    }

    /**
     * 根据租户ID获取数据实例列表
     * 
     * @param tenantId 租户ID
     * @return 数据实例列表
     */
    @Transactional(readOnly = true)
    public List<DataInstanceDTO> getDataInstancesByTenantId(String tenantId) {
        List<DataInstance> instances = dataInstanceRepository.findByTenantId(tenantId);
        return instances.stream()
                .map(dataInstanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 批量创建数据实例
     * 
     * @param dataInstanceDTOs 数据实例DTO列表
     * @return 创建的数据实例DTO列表
     */
    public List<DataInstanceDTO> batchCreateDataInstances(List<DataInstanceDTO> dataInstanceDTOs) {
        List<DataInstance> instances = dataInstanceDTOs.stream()
                .map(dto -> {
                    DataInstance instance = dataInstanceMapper.toEntity(dto);
                    instance.setCreatedAt(LocalDateTime.now());
                    instance.setUpdatedAt(LocalDateTime.now());
                    return instance;
                })
                .collect(Collectors.toList());
        
        List<DataInstance> savedInstances = dataInstanceRepository.saveAll(instances);
        return savedInstances.stream()
                .map(dataInstanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态获取数据实例列表
     * 
     * @param status 状态
     * @return 数据实例列表
     */
    @Transactional(readOnly = true)
    public List<DataInstanceDTO> getDataInstancesByStatus(String status) {
        List<DataInstance> instances = dataInstanceRepository.findByStatus(status);
        return instances.stream()
                .map(dataInstanceMapper::toDTO)
                .collect(Collectors.toList());
    }
} 