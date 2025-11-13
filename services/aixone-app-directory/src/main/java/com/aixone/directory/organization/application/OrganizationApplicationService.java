package com.aixone.directory.organization.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.organization.infrastructure.persistence.OrganizationJpaRepository;
import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aixone.directory.organization.application.dto.CreateDepartmentRequest;
import com.aixone.directory.organization.application.dto.CreatePositionRequest;
import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.application.dto.PositionDto;
import com.aixone.directory.organization.application.dto.CreateOrganizationRequest;
import com.aixone.directory.organization.application.dto.UpdateOrganizationRequest;
import com.aixone.directory.organization.application.dto.OrganizationDto;
import com.aixone.directory.organization.domain.aggregate.Department;
import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.domain.aggregate.Position;
import com.aixone.directory.organization.domain.repository.OrganizationRepository;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentMapper;
import com.aixone.directory.organization.infrastructure.persistence.PositionMapper;
import com.aixone.directory.organization.application.DepartmentDtoMapper;
import com.aixone.directory.organization.application.PositionDtoMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;
    private final DepartmentDtoMapper departmentDtoMapper;
    private final PositionDtoMapper positionDtoMapper;

    @Transactional
    public OrganizationDto createOrganization(CreateOrganizationRequest request) {
        organizationRepository.findByTenantIdAndName(request.getTenantId(), request.getName()).ifPresent(o -> {
            throw new IllegalStateException("Organization with this name already exists in the tenant.");
        });
        Organization organization = Organization.create(request.getTenantId(), request.getName());
        organizationRepository.save(organization);
        return toOrganizationDtoFromDbo(organizationJpaRepository.findById(organization.getId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found after creation")));
    }

    @Transactional
    public OrganizationDto updateOrganization(String organizationId, String tenantId, UpdateOrganizationRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));
        
        // 验证组织属于当前租户
        if (!organization.getTenantId().equals(tenantId)) {
            throw new IllegalStateException("Organization does not belong to the current tenant.");
        }
        
        // 如果名称改变，检查新名称是否已存在
        if (request.getName() != null && !request.getName().equals(organization.getName())) {
            organizationRepository.findByTenantIdAndName(tenantId, request.getName())
                    .filter(o -> !o.getId().equals(organizationId))
                    .ifPresent(o -> {
                        throw new IllegalStateException("Organization with this name already exists in the tenant.");
                    });
            organization.updateName(request.getName());
        }
        
        organizationRepository.save(organization);
        return toOrganizationDto(organization);
    }

    @Transactional
    public void deleteOrganization(String organizationId, String tenantId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));
        
        // 验证组织属于当前租户
        if (!organization.getTenantId().equals(tenantId)) {
            throw new IllegalStateException("Organization does not belong to the current tenant.");
        }
        
        // 检查是否有部门或岗位关联
        if (!organization.getDepartments().isEmpty()) {
            throw new IllegalStateException("Cannot delete organization with departments. Please delete departments first.");
        }
        if (!organization.getPositions().isEmpty()) {
            throw new IllegalStateException("Cannot delete organization with positions. Please delete positions first.");
        }
        
        organizationRepository.deleteById(organizationId);
    }

    @Transactional
    public DepartmentDto addDepartmentToOrganization(String organizationId, CreateDepartmentRequest request) {
        Organization organization = findOrganization(organizationId);
        Department newDepartment = organization.addDepartment(request.getName(), request.getParentId());
        organizationRepository.save(organization);
        return departmentDtoMapper.toDto(newDepartment);
    }

    @Transactional
    public PositionDto addPositionToOrganization(String organizationId, CreatePositionRequest request) {
        Organization organization = findOrganization(organizationId);
        Position newPosition = organization.addPosition(request.getName());
        organizationRepository.save(organization);
        return positionDtoMapper.toDto(newPosition);
    }

    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationById(String id) {
        return organizationRepository.findById(id)
                .map(this::toOrganizationDto)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.List<OrganizationDto> getOrganizations(String tenantId) {
        return organizationRepository.findByTenantId(tenantId).stream()
                .map(this::toOrganizationDto)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询组织列表（支持过滤）
     */
    @Transactional(readOnly = true)
    public PageResult<OrganizationDto> findOrganizations(PageRequest pageRequest, String tenantId, String name) {
        // 验证 tenantId 不能为空
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        
        // 构建查询规格
        Specification<OrganizationDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持name过滤
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：默认按创建时间倒序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("createdAt").descending();
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
            pageRequest.getPageSize(),
            sort
        );
        
        Page<OrganizationDbo> page = organizationJpaRepository.findAll(spec, pageable);
        List<OrganizationDto> content = page.getContent().stream()
                .map(this::toOrganizationDtoFromDbo)
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
    }

    private Organization findOrganization(String organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));
    }

    private OrganizationDto toOrganizationDto(Organization org) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(org.getId());
        dto.setTenantId(org.getTenantId());
        dto.setName(org.getName());
        dto.setCreatedAt(org.getCreatedAt());
        dto.setUpdatedAt(org.getUpdatedAt());
        // Note: In a real scenario, you might want pagination for departments/positions
        dto.setDepartments(
                org.getDepartments().stream()
                        .map(departmentDtoMapper::toDto)
                        .collect(Collectors.toList())
        );
        dto.setPositions(
                org.getPositions().stream()
                        .map(positionDtoMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    /**
     * 从 DBO 直接转换为 DTO（用于列表查询，不加载关联数据以提高性能）
     */
    private OrganizationDto toOrganizationDtoFromDbo(OrganizationDbo dbo) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(dbo.getId());
        dto.setTenantId(dbo.getTenantId());
        dto.setName(dbo.getName());
        dto.setCreatedAt(dbo.getCreatedAt());
        dto.setUpdatedAt(dbo.getUpdatedAt());
        // 列表查询不加载 departments 和 positions，避免 N+1 查询
        dto.setDepartments(null);
        dto.setPositions(null);
        return dto;
    }
} 