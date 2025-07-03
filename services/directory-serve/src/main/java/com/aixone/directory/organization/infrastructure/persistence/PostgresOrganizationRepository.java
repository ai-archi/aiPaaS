package com.aixone.directory.organization.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.domain.repository.OrganizationRepository;
import com.aixone.directory.organization.infrastructure.persistence.dbo.OrganizationDbo;

@Repository
public class PostgresOrganizationRepository implements OrganizationRepository {
    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationMapper organizationMapper;
    private final DepartmentMapper departmentMapper;
    private final PositionMapper positionMapper;

    @Autowired
    public PostgresOrganizationRepository(OrganizationJpaRepository organizationJpaRepository, OrganizationMapper organizationMapper, DepartmentMapper departmentMapper, PositionMapper positionMapper) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationMapper = organizationMapper;
        this.departmentMapper = departmentMapper;
        this.positionMapper = positionMapper;
    }

    @Override
    @Transactional
    public void save(Organization organization) {
        OrganizationDbo dbo = organizationMapper.toDboSimple(organization);
        dbo.setDepartments(organization.getDepartments().stream().map(departmentMapper::toDbo).collect(Collectors.toSet()));
        dbo.setPositions(organization.getPositions().stream().map(positionMapper::toDbo).collect(Collectors.toSet()));
        organizationJpaRepository.save(dbo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findById(String id) {
        return organizationJpaRepository.findById(id).map(dbo -> {
            Organization org = organizationMapper.toDomainSimple(dbo);
            org.getDepartments().addAll(dbo.getDepartments().stream().map(departmentMapper::toDomain).collect(Collectors.toSet()));
            org.getPositions().addAll(dbo.getPositions().stream().map(positionMapper::toDomain).collect(Collectors.toSet()));
            return org;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findByTenantIdAndName(String tenantId, String name) {
        return organizationJpaRepository.findByTenantIdAndName(tenantId, name)
                .map(dbo -> {
                    Organization org = organizationMapper.toDomainSimple(dbo);
                    org.getDepartments().addAll(dbo.getDepartments().stream().map(departmentMapper::toDomain).collect(Collectors.toSet()));
                    org.getPositions().addAll(dbo.getPositions().stream().map(positionMapper::toDomain).collect(Collectors.toSet()));
                    return org;
                });
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        organizationJpaRepository.deleteById(id);
    }
} 