package com.aixone.metacenter.permissionservice.application;

import com.aixone.metacenter.permissionservice.application.dto.PermissionDTO;
import com.aixone.metacenter.permissionservice.application.dto.PermissionQuery;
import com.aixone.metacenter.permissionservice.domain.Permission;
import com.aixone.metacenter.permissionservice.domain.PermissionRepository;
import com.aixone.metacenter.permissionservice.application.PermissionMapper;
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

@Service
@Transactional
public class PermissionApplicationService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toDTO(savedPermission);
    }

    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Optional<Permission> optional = permissionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("权限不存在: " + id);
        }
        Permission existingPermission = optional.get();
        permissionMapper.updateEntityFromDTO(permissionDTO, existingPermission);
        existingPermission.setUpdatedAt(LocalDateTime.now());
        Permission updatedPermission = permissionRepository.save(existingPermission);
        return permissionMapper.toDTO(updatedPermission);
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("权限不存在: " + id);
        }
        permissionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Optional<Permission> optional = permissionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("权限不存在: " + id);
        }
        return permissionMapper.toDTO(optional.get());
    }

    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByTenantId(String tenantId) {
        List<Permission> permissions = permissionRepository.findByTenantIdAndEnabledTrue(tenantId);
        return permissions.stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PermissionDTO> getPermissionsByTenantId(String tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Permission> permissions = permissionRepository.findByTenantId(tenantId, pageable);
        return permissions.map(permissionMapper::toDTO);
    }
}
