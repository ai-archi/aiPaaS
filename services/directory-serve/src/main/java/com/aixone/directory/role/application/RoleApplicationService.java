package com.aixone.directory.role.application;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.role.application.dto.AddMemberToRoleRequest;
import com.aixone.directory.role.application.dto.CreateRoleRequest;
import com.aixone.directory.role.application.dto.RoleDto;
import com.aixone.directory.role.domain.aggregate.Role;
import com.aixone.directory.role.domain.repository.RoleRepository;
import com.aixone.directory.role.infrastructure.persistence.RoleJpaRepository;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;
import com.aixone.directory.user.domain.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoleDto createRole(String tenantId, CreateRoleRequest request) {
        Role role = Role.create(UUID.fromString(tenantId), request.getName());
        roleRepository.save(role);
        return toDto(role, Collections.emptySet());
    }

    @Transactional
    public void addMemberToRole(UUID roleId, AddMemberToRoleRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserId()));
        
        RoleDbo roleDbo = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        roleDbo.getMembers().add(request.getUserId());
        roleJpaRepository.save(roleDbo);
    }
    
    @Transactional
    public void removeMemberFromRole(UUID tenantId, UUID roleId, UUID userId) {
        RoleDbo role = findRoleDboAndCheckTenant(tenantId, roleId);
        role.getMembers().remove(userId);
        roleJpaRepository.save(role);
    }
    
    public Optional<RoleDto> getRole(UUID roleId) {
        return roleJpaRepository.findById(roleId).map(this::toRoleDto);
    }

    private RoleDto toRoleDto(RoleDbo roleDbo) {
        Role role = new Role(roleDbo.getId(), roleDbo.getTenantId(), roleDbo.getName(), roleDbo.getCreatedAt(), roleDbo.getUpdatedAt());
        return toDto(role, roleDbo.getMembers());
    }

    private RoleDto toDto(Role role, Set<UUID> userIds) {
        return RoleDto.builder()
                .id(role.getId())
                .tenantId(role.getTenantId())
                .name(role.getName())
                .userIds(userIds)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    private RoleDbo findRoleDboAndCheckTenant(UUID tenantId, UUID roleId) {
        RoleDbo role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (!role.getTenantId().equals(tenantId)) {
            throw new SecurityException("Access denied to role.");
        }
        return role;
    }
} 