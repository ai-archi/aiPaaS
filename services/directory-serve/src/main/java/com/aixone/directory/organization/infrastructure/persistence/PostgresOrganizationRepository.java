package com.aixone.directory.organization.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.organization.domain.aggregate.Organization;
import com.aixone.directory.organization.domain.repository.OrganizationRepository;

@Repository
public class PostgresOrganizationRepository implements OrganizationRepository {
    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationMapper organizationMapper;

    public PostgresOrganizationRepository(OrganizationJpaRepository organizationJpaRepository, OrganizationMapper organizationMapper) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    @Transactional
    public void save(Organization organization) {
        organizationJpaRepository.save(organizationMapper.toDbo(organization));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findById(UUID id) {
        return organizationJpaRepository.findById(id).map(organizationMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findByTenantIdAndName(UUID tenantId, String name) {
        return organizationJpaRepository.findByTenantIdAndName(tenantId, name)
                .map(organizationMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        organizationJpaRepository.deleteById(id);
    }
} 