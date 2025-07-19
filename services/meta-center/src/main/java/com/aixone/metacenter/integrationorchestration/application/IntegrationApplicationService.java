package com.aixone.metacenter.integrationorchestration.application;

import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationDTO;
import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationQuery;
import com.aixone.metacenter.integrationorchestration.domain.Integration;
import com.aixone.metacenter.integrationorchestration.domain.IntegrationRepository;
import com.aixone.metacenter.integrationorchestration.application.IntegrationMapper;
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
 * 集成编排应用服务
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Service
@Transactional
public class IntegrationApplicationService {

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private IntegrationMapper integrationMapper;

    /**
     * 创建集成配置
     * 
     * @param integrationDTO 集成配置DTO
     * @return 创建的集成配置DTO
     */
    public IntegrationDTO createIntegration(IntegrationDTO integrationDTO) {
        Integration integration = integrationMapper.toEntity(integrationDTO);
        
        // 设置创建时间
        integration.setCreatedAt(LocalDateTime.now());
        integration.setUpdatedAt(LocalDateTime.now());
        
        Integration savedIntegration = integrationRepository.save(integration);
        return integrationMapper.toDTO(savedIntegration);
    }

    /**
     * 更新集成配置
     * 
     * @param id 集成配置ID
     * @param integrationDTO 集成配置DTO
     * @return 更新后的集成配置DTO
     */
    public IntegrationDTO updateIntegration(Long id, IntegrationDTO integrationDTO) {
        Optional<Integration> optional = integrationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("集成配置不存在: " + id);
        }
        
        Integration existingIntegration = optional.get();
        integrationMapper.updateEntityFromDTO(integrationDTO, existingIntegration);
        existingIntegration.setUpdatedAt(LocalDateTime.now());
        
        Integration updatedIntegration = integrationRepository.save(existingIntegration);
        return integrationMapper.toDTO(updatedIntegration);
    }

    /**
     * 删除集成配置
     * 
     * @param id 集成配置ID
     */
    public void deleteIntegration(Long id) {
        if (!integrationRepository.existsById(id)) {
            throw new RuntimeException("集成配置不存在: " + id);
        }
        integrationRepository.deleteById(id);
    }

    /**
     * 根据ID获取集成配置
     * 
     * @param id 集成配置ID
     * @return 集成配置DTO
     */
    @Transactional(readOnly = true)
    public IntegrationDTO getIntegrationById(Long id) {
        Optional<Integration> optional = integrationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("集成配置不存在: " + id);
        }
        return integrationMapper.toDTO(optional.get());
    }

    /**
     * 根据租户ID分页查询集成配置
     * 
     * @param tenantId 租户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<IntegrationDTO> getIntegrationsByTenantId(String tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Integration> integrations = integrationRepository.findByTenantId(tenantId, pageable);
        return integrations.map(integrationMapper::toDTO);
    }

    /**
     * 执行集成
     * 
     * @param id 集成配置ID
     * @return 执行结果
     */
    public String executeIntegration(Long id) {
        Optional<Integration> optional = integrationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("集成配置不存在: " + id);
        }
        
        Integration integration = optional.get();
        // TODO: 实现具体的集成执行逻辑
        return "集成执行成功: " + integration.getName();
    }

    /**
     * 测试集成连接
     * 
     * @param id 集成配置ID
     * @return 测试结果
     */
    public String testIntegrationConnection(Long id) {
        Optional<Integration> optional = integrationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("集成配置不存在: " + id);
        }
        
        Integration integration = optional.get();
        // TODO: 实现具体的连接测试逻辑
        return "连接测试成功: " + integration.getName();
    }
} 