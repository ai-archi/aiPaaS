package com.aixone.directory.group.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aixone.directory.group.application.dto.AddMemberRequest;
import com.aixone.directory.group.application.dto.CreateGroupRequest;
import com.aixone.directory.group.application.dto.GroupDto;
import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.domain.repository.GroupRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupApplicationService {

    private final GroupRepository groupRepository;

    @Transactional
    public GroupDto createGroup(UUID tenantId, CreateGroupRequest request) {
        Group newGroup = Group.create(tenantId, request.getName());
        groupRepository.save(newGroup);
        return toDto(newGroup);
    }

    @Transactional
    public void addMemberToGroup(UUID tenantId, UUID groupId, AddMemberRequest request) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        group.addMember(request.getUserId());
        groupRepository.save(group);
    }
    
    @Transactional
    public void removeMemberFromGroup(UUID tenantId, UUID groupId, UUID userId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        group.removeMember(userId);
        groupRepository.save(group);
    }
    
    @Transactional(readOnly = true)
    public GroupDto getGroup(UUID tenantId, UUID groupId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        return toDto(group);
    }

    private Group findGroupAndCheckTenant(UUID tenantId, UUID groupId) {
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