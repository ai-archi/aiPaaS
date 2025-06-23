package com.aixone.directory.organization.application;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.organization.application.dto.CreateDepartmentRequest;
import com.aixone.directory.organization.application.dto.CreatePositionRequest;
import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.application.dto.PositionDto;
import com.aixone.directory.organization.application.dto.CreateOrganizationRequest;
import com.aixone.directory.organization.application.dto.OrganizationDto;
import com.aixone.directory.organization.domain.aggregate.Department;
import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.domain.aggregate.Position;
import com.aixone.directory.organization.domain.repository.OrganizationRepository;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentMapper;
import com.aixone.directory.organization.infrastructure.persistence.PositionMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;

    @Transactional
    public UUID createOrganization(CreateOrganizationRequest request) {
        // Optional: Check for duplicate names within the same tenant
        organizationRepository.findByTenantIdAndName(request.getTenantId(), request.getName()).ifPresent(o -> {
            throw new IllegalStateException("Organization with this name already exists in the tenant.");
        });

        Organization organization = Organization.create(request.getTenantId(), request.getName());
        organizationRepository.save(organization);
        return organization.getId();
    }

    @Transactional
    public DepartmentDto addDepartmentToOrganization(UUID organizationId, CreateDepartmentRequest request) {
        Organization organization = findOrganization(organizationId);
        Department newDepartment = organization.addDepartment(request.getName(), request.getParentId());
        organizationRepository.save(organization);
        return departmentMapper.toDto(newDepartment);
    }

    @Transactional
    public PositionDto addPositionToOrganization(UUID organizationId, CreatePositionRequest request) {
        Organization organization = findOrganization(organizationId);
        Position newPosition = organization.addPosition(request.getName());
        organizationRepository.save(organization);
        return positionMapper.toDto(newPosition);
    }

    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationById(UUID id) {
        return organizationRepository.findById(id)
                .map(this::toOrganizationDto)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
    }

    private Organization findOrganization(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + organizationId));
    }

    private OrganizationDto toOrganizationDto(Organization org) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setCreatedAt(org.getCreatedAt());
        dto.setUpdatedAt(org.getUpdatedAt());
        // Note: In a real scenario, you might want pagination for departments/positions
        dto.setDepartments(
                org.getDepartments().stream()
                        .map(departmentMapper::toDto)
                        .collect(Collectors.toList())
        );
        dto.setPositions(
                org.getPositions().stream()
                        .map(positionMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
} 