package com.aixone.directory.group.application;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.group.application.dto.AddMemberRequest;
import com.aixone.directory.group.application.dto.CreateGroupRequest;
import com.aixone.directory.group.application.dto.GroupDto;
import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.domain.repository.GroupRepository;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;
import com.aixone.directory.group.infrastructure.persistence.GroupJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import com.aixone.directory.role.infrastructure.persistence.RoleJpaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupApplicationService {

    private final GroupRepository groupRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Transactional
    public GroupDto createGroup(String tenantId, CreateGroupRequest request) {
        Group newGroup = Group.create(tenantId, request.getName());
        groupRepository.save(newGroup);
        return toDto(newGroup);
    }

    @Transactional
    public void addMemberToGroup(String tenantId, String groupId, AddMemberRequest request) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        group.addMember(request.getUserId());
        groupRepository.save(group);
    }
    
    @Transactional
    public void removeMemberFromGroup(String tenantId, String groupId, String userId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        group.removeMember(userId);
        groupRepository.save(group);
    }
    
    @Transactional(readOnly = true)
    public GroupDto getGroup(String tenantId, String groupId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        return toDto(group);
    }

    @Transactional
    public void assignUsersToGroup(String groupId, Set<String> userIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        Set<UserDbo> users = new java.util.HashSet<>(userJpaRepository.findAllById(userIds));
        group.getUsers().addAll(users);
        groupJpaRepository.save(group);
    }

    @Transactional
    public void removeUsersFromGroup(String groupId, Set<String> userIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        group.getUsers().removeIf(u -> userIds.contains(u.getId()));
        groupJpaRepository.save(group);
    }

    @Transactional
    public void assignRolesToGroup(String groupId, Set<String> roleIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        Set<RoleDbo> roles = new java.util.HashSet<>(roleJpaRepository.findAllById(roleIds));
        group.getRoles().addAll(roles);
        groupJpaRepository.save(group);
    }

    @Transactional
    public void removeRolesFromGroup(String groupId, Set<String> roleIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        group.getRoles().removeIf(r -> roleIds.contains(r.getId()));
        groupJpaRepository.save(group);
    }

    private Group findGroupAndCheckTenant(String tenantId, String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        if (!group.getTenantId().equals(tenantId)) {
            throw new SecurityException("Access denied to group.");
        }
        return group;
    }

    private GroupDto toDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .tenantId(group.getTenantId())
                .name(group.getName())
                .members(group.getMembers())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
} 