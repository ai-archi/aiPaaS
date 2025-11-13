package com.aixone.directory.permission.infrastructure.persistence;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.infrastructure.persistence.dbo.PermissionDbo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 权限仓储实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class PostgresPermissionRepository implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Permission save(Permission permission) {
        PermissionDbo dbo = convertToDbo(permission);
        PermissionDbo savedDbo = permissionJpaRepository.save(dbo);
        return convertToDomain(savedDbo);
    }

    @Override
    public Optional<Permission> findById(String permissionId) {
        return permissionJpaRepository.findById(permissionId)
                .map(this::convertToDomain);
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        List<PermissionDbo> dbos = permissionJpaRepository.findByTenantId(tenantId);
        return dbos.stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Permission> findByTenantIdAndCode(String tenantId, String code) {
        return permissionJpaRepository.findByTenantIdAndCode(tenantId, code)
                .map(this::convertToDomain);
    }

    @Override
    public Optional<Permission> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action) {
        return permissionJpaRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action)
                .map(this::convertToDomain);
    }

    @Override
    public void delete(String permissionId) {
        permissionJpaRepository.deleteById(permissionId);
    }

    @Override
    public boolean existsById(String permissionId) {
        return permissionJpaRepository.existsById(permissionId);
    }

    @Override
    public boolean existsByTenantIdAndCode(String tenantId, String code) {
        return permissionJpaRepository.existsByTenantIdAndCode(tenantId, code);
    }

    /**
     * 转换为数据库对象
     */
    private PermissionDbo convertToDbo(Permission permission) {
        PermissionDbo.PermissionDboBuilder builder = PermissionDbo.builder()
                .tenantId(permission.getTenantId())
                .name(permission.getName())
                .code(permission.getCode())
                .resource(permission.getResource())
                .action(permission.getAction())
                .type(permission.getType() != null ? permission.getType().name() : "FUNCTIONAL")
                .description(permission.getDescription())
                .abacConditions(permission.getAbacConditions())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt());
        
        // 如果ID为空，生成新的UUID
        if (permission.getPermissionId() == null || permission.getPermissionId().isEmpty()) {
            builder.permissionId(UUID.randomUUID().toString());
        } else {
            builder.permissionId(permission.getPermissionId());
        }
        
        return builder.build();
    }

    /**
     * 转换为领域对象
     */
    private Permission convertToDomain(PermissionDbo dbo) {
        return Permission.builder()
                .permissionId(dbo.getPermissionId())
                .tenantId(dbo.getTenantId())
                .name(dbo.getName())
                .code(dbo.getCode())
                .resource(dbo.getResource())
                .action(dbo.getAction())
                .type(dbo.getType() != null ? Permission.PermissionType.valueOf(dbo.getType()) : Permission.PermissionType.FUNCTIONAL)
                .description(dbo.getDescription())
                .abacConditions(dbo.getAbacConditions())
                .createdAt(dbo.getCreatedAt())
                .updatedAt(dbo.getUpdatedAt())
                .build();
    }
}

