package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.command.AssignUserRoleCommand;
import com.aixone.tech.auth.authorization.application.dto.UserRoleResponse;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户角色管理应用服务
 */
@Service
@Transactional
public class UserRoleManagementApplicationService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserRoleManagementApplicationService(UserRoleRepository userRoleRepository, 
                                               RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 分配用户角色
     */
    public UserRoleResponse assignUserRole(AssignUserRoleCommand command) {
        // 1. 验证角色是否存在
        Role role = roleRepository.findByTenantIdAndRoleId(command.getTenantId(), command.getRoleId());
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 2. 检查用户是否已经有该角色
        if (userRoleRepository.existsByTenantIdAndUserIdAndRoleId(
                command.getTenantId(), command.getUserId(), command.getRoleId())) {
            throw new IllegalArgumentException("用户已经拥有该角色");
        }

        // 3. 创建用户角色关联
        String userRoleId = UUID.randomUUID().toString();
        UserRole userRole = new UserRole(
            userRoleId,
            command.getTenantId(),
            command.getUserId(),
            command.getRoleId(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        UserRole savedUserRole = userRoleRepository.save(userRole);

        // 4. 返回响应
        return new UserRoleResponse(
            savedUserRole.getUserRoleId(),
            savedUserRole.getTenantId(),
            savedUserRole.getUserId(),
            savedUserRole.getRoleId(),
            role.getName(),
            role.getDescription(),
            savedUserRole.getCreatedAt(),
            savedUserRole.getUpdatedAt()
        );
    }

    /**
     * 移除用户角色
     */
    public void removeUserRole(String tenantId, String userId, String roleId) {
        // 1. 验证用户角色是否存在
        if (!userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId)) {
            throw new IllegalArgumentException("用户角色关联不存在");
        }

        // 2. 删除用户角色关联
        userRoleRepository.deleteByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }

    /**
     * 查询用户的所有角色
     */
    @Transactional(readOnly = true)
    public List<UserRoleResponse> getUserRoles(String tenantId, String userId) {
        List<UserRole> userRoles = userRoleRepository.findByTenantIdAndUserId(tenantId, userId);
        
        return userRoles.stream()
                .map(userRole -> {
                    Role role = roleRepository.findByTenantIdAndRoleId(tenantId, userRole.getRoleId());
                    return new UserRoleResponse(
                        userRole.getUserRoleId(),
                        userRole.getTenantId(),
                        userRole.getUserId(),
                        userRole.getRoleId(),
                        role != null ? role.getName() : "未知角色",
                        role != null ? role.getDescription() : "",
                        userRole.getCreatedAt(),
                        userRole.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否拥有指定角色
     */
    @Transactional(readOnly = true)
    public boolean hasRole(String tenantId, String userId, String roleId) {
        return userRoleRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }
}
