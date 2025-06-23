package com.aixone.directory.role.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.role.application.dto.AddMemberToRoleRequest;
import com.aixone.directory.role.application.dto.CreateRoleRequest;
import com.aixone.directory.role.application.dto.RoleDto;
import com.aixone.directory.role.domain.aggregate.Role;
import com.aixone.directory.role.domain.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleDto createRole(UUID tenantId, CreateRoleRequest request) {
        roleRepository.findByTenantIdAndName(tenantId, request.getName()).ifPresent(r -> {
            throw new IllegalStateException("Role with name '" + request.getName() + "' already exists in this tenant.");
        });

        Role newRole = Role.create(tenantId, request.getName());
        roleRepository.save(newRole);
        return toDto(newRole);
    }

    @Transactional
    public void addMemberToRole(UUID tenantId, UUID roleId, AddMemberToRoleRequest request) {
        Role role = findRoleAndCheckTenant(tenantId, roleId);
        role.addMember(request.getUserId());
        roleRepository.save(role);
    }
    
    @Transactional
    public void removeMemberFromRole(UUID tenantId, UUID roleId, UUID userId) {
        Role role = findRoleAndCheckTenant(tenantId, roleId);
        role.removeMember(userId);
        roleRepository.save(role);
    }
    
    @Transactional(readOnly = true)
    public RoleDto getRole(UUID tenantId, UUID roleId) {
        Role role = findRoleAndCheckTenant(tenantId, roleId);
        return toDto(role);
    }

    private Role findRoleAndCheckTenant(UUID tenantId, UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (!role.getTenantId().equals(tenantId)) {
            throw new SecurityException("Access denied to role.");
        }
        return role;
    }

    private RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .tenantId(role.getTenantId())
                .name(role.getName())
                .members(role.getMembers())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
} 