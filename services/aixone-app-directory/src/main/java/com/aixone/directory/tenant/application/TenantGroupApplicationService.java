package com.aixone.directory.tenant.application;

import com.aixone.common.exception.BizException;
import com.aixone.directory.tenant.application.dto.TenantGroupDto;
import com.aixone.directory.tenant.domain.aggregate.TenantGroup;
import com.aixone.directory.tenant.domain.repository.TenantGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 租户组应用服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TenantGroupApplicationService {

    private final TenantGroupRepository tenantGroupRepository;

    /**
     * 创建租户组
     */
    @Transactional
    public TenantGroupDto createTenantGroup(TenantGroupDto.CreateTenantGroupRequest request) {
        log.info("创建租户组: name={}", request.getName());

        // 检查名称是否已存在
        if (tenantGroupRepository.existsByName(request.getName())) {
            throw new BizException("TENANT_GROUP_NAME_EXISTS", "租户组名称已存在");
        }

        TenantGroup tenantGroup = TenantGroup.create(
                request.getName(),
                request.getDescription(),
                request.getParentId()
        );

        if (request.getSortOrder() != null) {
            tenantGroup.updateSortOrder(request.getSortOrder());
        }

        TenantGroup saved = tenantGroupRepository.save(tenantGroup);
        return convertToDto(saved);
    }

    /**
     * 获取租户组
     */
    public Optional<TenantGroupDto> getTenantGroup(String id) {
        return tenantGroupRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * 获取所有租户组
     */
    public List<TenantGroupDto> getAllTenantGroups() {
        return tenantGroupRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * 根据父ID获取租户组
     */
    public List<TenantGroupDto> getTenantGroupsByParent(String parentId) {
        return tenantGroupRepository.findByParentId(parentId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * 获取根租户组
     */
    public List<TenantGroupDto> getRootTenantGroups() {
        return tenantGroupRepository.findRootGroups().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * 更新租户组
     */
    @Transactional
    public TenantGroupDto updateTenantGroup(String id, TenantGroupDto.UpdateTenantGroupRequest request) {
        log.info("更新租户组: id={}", id);

        TenantGroup tenantGroup = tenantGroupRepository.findById(id)
                .orElseThrow(() -> new BizException("TENANT_GROUP_NOT_FOUND", "租户组不存在"));

        if (request.getName() != null && !request.getName().equals(tenantGroup.getName())) {
            // 检查新名称是否已存在
            if (tenantGroupRepository.existsByName(request.getName())) {
                throw new BizException("TENANT_GROUP_NAME_EXISTS", "租户组名称已存在");
            }
            tenantGroup.updateName(request.getName());
        }

        if (request.getDescription() != null) {
            tenantGroup.updateDescription(request.getDescription());
        }

        if (request.getParentId() != null) {
            tenantGroup.updateParent(request.getParentId());
        }

        if (request.getSortOrder() != null) {
            tenantGroup.updateSortOrder(request.getSortOrder());
        }

        TenantGroup saved = tenantGroupRepository.save(tenantGroup);
        return convertToDto(saved);
    }

    /**
     * 删除租户组
     */
    @Transactional
    public void deleteTenantGroup(String id) {
        log.info("删除租户组: id={}", id);

        TenantGroup tenantGroup = tenantGroupRepository.findById(id)
                .orElseThrow(() -> new BizException("TENANT_GROUP_NOT_FOUND", "租户组不存在"));

        // 软删除
        tenantGroup.markAsDeleted();
        tenantGroupRepository.save(tenantGroup);
    }

    /**
     * 激活租户组
     */
    @Transactional
    public void activateTenantGroup(String id) {
        TenantGroup tenantGroup = tenantGroupRepository.findById(id)
                .orElseThrow(() -> new BizException("TENANT_GROUP_NOT_FOUND", "租户组不存在"));
        tenantGroup.activate();
        tenantGroupRepository.save(tenantGroup);
    }

    /**
     * 停用租户组
     */
    @Transactional
    public void deactivateTenantGroup(String id) {
        TenantGroup tenantGroup = tenantGroupRepository.findById(id)
                .orElseThrow(() -> new BizException("TENANT_GROUP_NOT_FOUND", "租户组不存在"));
        tenantGroup.deactivate();
        tenantGroupRepository.save(tenantGroup);
    }

    /**
     * 转换为DTO
     */
    private TenantGroupDto convertToDto(TenantGroup tenantGroup) {
        return TenantGroupDto.builder()
                .id(tenantGroup.getId())
                .name(tenantGroup.getName())
                .description(tenantGroup.getDescription())
                .parentId(tenantGroup.getParentId())
                .sortOrder(tenantGroup.getSortOrder())
                .status(tenantGroup.getStatus())
                .createdAt(tenantGroup.getCreatedAt())
                .updatedAt(tenantGroup.getUpdatedAt())
                .build();
    }
}

