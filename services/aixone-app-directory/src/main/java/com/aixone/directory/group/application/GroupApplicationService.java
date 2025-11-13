package com.aixone.directory.group.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.group.application.dto.AddMemberRequest;
import com.aixone.directory.group.application.dto.CreateGroupRequest;
import com.aixone.directory.group.application.dto.GroupDto;
import com.aixone.directory.group.application.dto.UpdateGroupRequest;
import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.domain.repository.GroupRepository;
import com.aixone.directory.group.infrastructure.persistence.GroupMapper;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;
import com.aixone.directory.group.infrastructure.persistence.GroupJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.UserMapper;
import com.aixone.directory.user.application.UserDto;
import com.aixone.directory.user.application.UserDtoMapper;
import com.aixone.directory.role.infrastructure.persistence.RoleJpaRepository;
import com.aixone.directory.role.application.dto.RoleDto;
import com.aixone.directory.role.infrastructure.persistence.RoleMapper;
import com.aixone.directory.role.domain.aggregate.Role;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupApplicationService {

    private final GroupRepository groupRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final GroupMapper groupMapper;
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    private final UserDtoMapper userDtoMapper;
    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

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
    public GroupDto updateGroup(String tenantId, String groupId, UpdateGroupRequest request) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        if (request.getName() != null && !request.getName().equals(group.getName())) {
            group.updateName(request.getName());
        }
        groupRepository.save(group);
        return toDto(group);
    }

    @Transactional
    public void deleteGroup(String tenantId, String groupId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        groupRepository.deleteById(groupId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getGroupMembers(String tenantId, String groupId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        GroupDbo groupDbo = groupJpaRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        
        return groupDbo.getUsers().stream()
                .map(userMapper::toDomain)
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getGroupRoles(String tenantId, String groupId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        GroupDbo groupDbo = groupJpaRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        
        return groupDbo.getRoles().stream()
                .map(roleMapper::toDomain)
                .map(role -> RoleDto.builder()
                        .id(role.getId())
                        .tenantId(role.getTenantId())
                        .name(role.getName())
                        .createdAt(role.getCreatedAt())
                        .updatedAt(role.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void replaceGroupMembers(String tenantId, String groupId, Set<String> userIds) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        GroupDbo groupDbo = groupJpaRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        
        // 验证所有用户都属于当前租户
        List<UserDbo> users = userJpaRepository.findAllById(userIds);
        for (UserDbo user : users) {
            if (!user.getTenantId().equals(tenantId)) {
                throw new SecurityException("User does not belong to the current tenant.");
            }
        }
        
        // 批量替换成员
        groupDbo.getUsers().clear();
        groupDbo.getUsers().addAll(users);
        groupJpaRepository.save(groupDbo);
    }

    @Transactional
    public void addMemberToGroup(String tenantId, String groupId, String userId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        
        // 验证用户属于当前租户
        UserDbo user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!user.getTenantId().equals(tenantId)) {
            throw new SecurityException("User does not belong to the current tenant.");
        }
        
        group.addMember(userId);
        groupRepository.save(group);
    }

    @Transactional
    public void replaceGroupRoles(String tenantId, String groupId, Set<String> roleIds) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        GroupDbo groupDbo = groupJpaRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));
        
        // 验证所有角色都属于当前租户
        List<RoleDbo> roles = roleJpaRepository.findAllById(roleIds);
        for (RoleDbo role : roles) {
            if (!role.getTenantId().equals(tenantId)) {
                throw new SecurityException("Role does not belong to the current tenant.");
            }
        }
        
        // 批量替换角色
        groupDbo.getRoles().clear();
        groupDbo.getRoles().addAll(roles);
        groupJpaRepository.save(groupDbo);
    }

    @Transactional
    public void addRoleToGroup(String tenantId, String groupId, String roleId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        
        // 验证角色属于当前租户
        RoleDbo role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        if (!role.getTenantId().equals(tenantId)) {
            throw new SecurityException("Role does not belong to the current tenant.");
        }
        
        group.addRole(roleId);
        groupRepository.save(group);
    }

    @Transactional
    public void removeRoleFromGroup(String tenantId, String groupId, String roleId) {
        Group group = findGroupAndCheckTenant(tenantId, groupId);
        group.removeRole(roleId);
        groupRepository.save(group);
    }

    // 保留旧方法以兼容（已废弃，建议使用新方法）
    @Deprecated
    @Transactional
    public void assignUsersToGroup(String groupId, Set<String> userIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        Set<UserDbo> users = new java.util.HashSet<>(userJpaRepository.findAllById(userIds));
        group.getUsers().addAll(users);
        groupJpaRepository.save(group);
    }

    @Deprecated
    @Transactional
    public void removeUsersFromGroup(String groupId, Set<String> userIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        group.getUsers().removeIf(u -> userIds.contains(u.getId()));
        groupJpaRepository.save(group);
    }

    @Deprecated
    @Transactional
    public void assignRolesToGroup(String groupId, Set<String> roleIds) {
        GroupDbo group = groupJpaRepository.findById(groupId).orElseThrow();
        Set<RoleDbo> roles = new java.util.HashSet<>(roleJpaRepository.findAllById(roleIds));
        group.getRoles().addAll(roles);
        groupJpaRepository.save(group);
    }

    @Deprecated
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

    /**
     * 分页查询群组列表（支持过滤）
     */
    @Transactional(readOnly = true)
    public PageResult<GroupDto> findGroups(PageRequest pageRequest, String tenantId, String name) {
        // 验证 tenantId 不能为空
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        
        // 构建查询规格
        Specification<GroupDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持name过滤
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：默认按创建时间倒序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("createdAt").descending();
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
            pageRequest.getPageSize(),
            sort
        );
        
        Page<GroupDbo> page = groupJpaRepository.findAll(spec, pageable);
        List<GroupDto> content = page.getContent().stream()
                .map(groupMapper::toDomain)
                .map(this::toDto)
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
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