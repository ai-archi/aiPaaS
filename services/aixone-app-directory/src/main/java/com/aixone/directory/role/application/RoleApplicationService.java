package com.aixone.directory.role.application;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;
import com.aixone.directory.group.infrastructure.persistence.GroupJpaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;

    @Transactional
    public RoleDto createRole(String tenantId, CreateRoleRequest request) {
        Role role = Role.create(tenantId, request.getName());
        roleRepository.save(role);
        return toDto(role, Collections.emptySet());
    }

    @Transactional
    public void addMemberToRole(String roleId, AddMemberToRoleRequest request) {
        UserDbo userDbo = userJpaRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserId()));
        RoleDbo roleDbo = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));
        roleDbo.getUsers().add(userDbo);
        roleJpaRepository.save(roleDbo);
    }
    
    @Transactional
    public void removeMemberFromRole(String tenantId, String roleId, String userId) {
        RoleDbo role = findRoleDboAndCheckTenant(tenantId, roleId);
        role.getUsers().removeIf(u -> u.getId().equals(userId));
        roleJpaRepository.save(role);
    }
    
    public Optional<RoleDto> getRole(String roleId) {
        return roleJpaRepository.findById(roleId).map(this::toRoleDto);
    }

    @Transactional
    public void assignUsersToRole(String roleId, Set<String> userIds) {
        RoleDbo role = roleJpaRepository.findById(roleId).orElseThrow();
        Set<UserDbo> users = new java.util.HashSet<>(userJpaRepository.findAllById(userIds));
        role.getUsers().addAll(users);
        roleJpaRepository.save(role);
    }

    @Transactional
    public void removeUsersFromRole(String roleId, Set<String> userIds) {
        RoleDbo role = roleJpaRepository.findById(roleId).orElseThrow();
        role.getUsers().removeIf(u -> userIds.contains(u.getId()));
        roleJpaRepository.save(role);
    }

    @Transactional
    public void assignGroupsToRole(String roleId, Set<String> groupIds) {
        RoleDbo role = roleJpaRepository.findById(roleId).orElseThrow();
        Set<GroupDbo> groups = new java.util.HashSet<>(groupJpaRepository.findAllById(groupIds));
        role.getGroups().addAll(groups);
        roleJpaRepository.save(role);
    }

    @Transactional
    public void removeGroupsFromRole(String roleId, Set<String> groupIds) {
        RoleDbo role = roleJpaRepository.findById(roleId).orElseThrow();
        role.getGroups().removeIf(g -> groupIds.contains(g.getId()));
        roleJpaRepository.save(role);
    }

    private RoleDto toRoleDto(RoleDbo roleDbo) {
        Role role = new Role(roleDbo.getId(), roleDbo.getTenantId(), roleDbo.getName(), roleDbo.getCreatedAt(), roleDbo.getUpdatedAt());
        java.util.Set<String> userIds = new java.util.HashSet<>();
        for (UserDbo user : roleDbo.getUsers()) {
            userIds.add(user.getId());
        }
        return toDto(role, userIds);
    }

    private RoleDto toDto(Role role, Set<String> userIds) {
        return RoleDto.builder()
                .id(role.getId())
                .tenantId(role.getTenantId())
                .name(role.getName())
                .userIds(userIds)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    private RoleDbo findRoleDboAndCheckTenant(String tenantId, String roleId) {
        RoleDbo role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (!role.getTenantId().equals(tenantId)) {
            throw new SecurityException("Access denied to role.");
        }
        return role;
    }
} 