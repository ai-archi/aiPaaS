package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CreatePermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.PermissionResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdatePermissionRequest;
import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionManagementApplicationService {

    private final PermissionRepository permissionRepository;

    public PermissionManagementApplicationService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByTenantIdAndName(request.getTenantId(), request.getName())) {
            throw new IllegalArgumentException("权限名称已存在");
        }

        Permission permission = new Permission(
            UUID.randomUUID().toString(),
            request.getTenantId(),
            request.getName(),
            request.getResource(),
            request.getAction(),
            request.getDescription()
        );
        Permission savedPermission = permissionRepository.save(permission);
        return toPermissionResponse(savedPermission);
    }

    public PermissionResponse updatePermission(String permissionId, UpdatePermissionRequest request) {
        Permission existingPermission = permissionRepository.findByTenantIdAndPermissionId(request.getTenantId(), permissionId);
        if (existingPermission == null) {
            throw new IllegalArgumentException("权限不存在");
        }

        if (!existingPermission.getName().equals(request.getName()) && permissionRepository.existsByTenantIdAndName(request.getTenantId(), request.getName())) {
            throw new IllegalArgumentException("权限名称已存在");
        }

        existingPermission.setName(request.getName());
        existingPermission.setResource(request.getResource());
        existingPermission.setAction(request.getAction());
        existingPermission.setDescription(request.getDescription());
        existingPermission.setUpdatedAt(LocalDateTime.now());

        Permission updatedPermission = permissionRepository.save(existingPermission);
        return toPermissionResponse(updatedPermission);
    }

    public void deletePermission(String tenantId, String permissionId) {
        permissionRepository.deleteByTenantIdAndPermissionId(tenantId, permissionId);
    }

    public PermissionResponse getPermissionById(String tenantId, String permissionId) {
        Permission permission = permissionRepository.findByTenantIdAndPermissionId(tenantId, permissionId);
        if (permission == null) {
            throw new IllegalArgumentException("权限不存在");
        }
        return toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions(String tenantId) {
        return permissionRepository.findByTenantId(tenantId)
            .stream()
            .map(this::toPermissionResponse)
            .collect(Collectors.toList());
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(
            permission.getPermissionId(),
            permission.getTenantId(),
            permission.getName(),
            permission.getResource(),
            permission.getAction(),
            permission.getDescription(),
            permission.getCreatedAt(),
            permission.getUpdatedAt()
        );
    }
}
