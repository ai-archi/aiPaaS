package com.aixone.directory.tenant.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.domain.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantApplicationService {

    private final TenantRepository tenantRepository;

    public String createTenant(TenantDto.CreateTenantCommand command) {
        Assert.notNull(command, "Command must not be null");
        Assert.hasText(command.getName(), "Tenant name must not be empty");

        Tenant tenant = Tenant.create(command.getName());
        tenantRepository.save(tenant);
        // In a real app, you would publish a TenantCreatedEvent here
        return tenant.getId();
    }

    public Optional<TenantDto.TenantView> findTenantById(String tenantId) {
        return tenantRepository.findById(tenantId)
                .map(tenant -> TenantDto.TenantView.builder()
                        .id(tenant.getId())
                        .name(tenant.getName())
                        .status(tenant.getStatus().name())
                        .createdAt(tenant.getCreatedAt())
                        .build());
    }
} 