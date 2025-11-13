package com.aixone.directory.user.application;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.domain.aggregate.UserStatus;
import com.aixone.directory.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.UserMapper;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentJpaRepository;
import com.aixone.directory.organization.infrastructure.persistence.PositionJpaRepository;
import com.aixone.directory.role.infrastructure.persistence.RoleJpaRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final PositionJpaRepository positionJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Transactional
    public UserDto createUser(String tenantId, UserDto.CreateUserCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        Assert.isTrue(userRepository.findByTenantIdAndEmail(tenantId, command.getEmail()).isEmpty(), "Email already exists in this tenant");
        User user = User.createUser(tenantId, command.getEmail(), command.getPassword(), command.getUsername(), passwordEncoder);
        userRepository.save(user);
        return userDtoMapper.toDto(user);
    }

    @Transactional
    public void updateUserProfile(String tenantId, String userId, UserDto.UpdateProfileCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        User user = userRepository.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found in this tenant"));
        Profile newProfile = Profile.builder()
                .username(command.getUsername())
                .avatarUrl(command.getAvatarUrl())
                .bio(command.getBio())
                .build();
        user.updateProfile(newProfile);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String tenantId, String userId, UserDto.ChangePasswordCommand command) {
        User user = userRepository.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.changePassword(command.getNewPassword(), passwordEncoder);
        userRepository.save(user);
    }

    public Optional<UserDto> getUser(String tenantId, String userId) {
        return userRepository.findByTenantIdAndId(tenantId, userId)
                .map(userDtoMapper::toDto);
    }

    public java.util.List<UserDto> getUsers(String tenantId) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantId(tenantId).stream()
                .map(userDtoMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 分页查询用户列表（支持过滤）
     */
    @Transactional(readOnly = true)
    public PageResult<UserDto> findUsers(PageRequest pageRequest, String tenantId, String username, String email, String status) {
        // 验证 tenantId 不能为空
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        
        // 构建查询规格
        Specification<UserDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持username过滤
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }
            
            // 支持email过滤
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            
            // 支持status过滤
            if (StringUtils.hasText(status)) {
                try {
                    UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), userStatus));
                } catch (IllegalArgumentException e) {
                    // 如果status值无效，忽略该过滤条件
                }
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
        
        Page<UserDbo> page = userJpaRepository.findAll(spec, pageable);
        List<UserDto> content = page.getContent().stream()
                .map(userMapper::toDomain)
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
    }

    public Optional<UserDto> getUserByEmail(String tenantId, String email) {
        return userRepository.findByTenantIdAndEmail(tenantId, email)
                .map(userDtoMapper::toDto);
    }

    public Optional<UserDto.UserPublicView> findUserById(String tenantId, String userId) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndId(tenantId, userId)
                .map(user -> UserDto.UserPublicView.builder()
                        .id(user.getId())
                        .username(user.getProfile().getUsername())
                        .avatarUrl(user.getProfile().getAvatarUrl())
                        .build());
    }

    public Optional<UserDto.UserCredentialsView> findUserCredentialsByEmail(String tenantId, String email) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndEmail(tenantId, email)
                .map(user -> UserDto.UserCredentialsView.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .hashedPassword(user.getHashedPassword())
                        .status(user.getStatus())
                        .build());
    }

    @Transactional
    public void assignDepartmentsToUser(String userId, Set<String> departmentIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        Set<DepartmentDbo> departments = new java.util.HashSet<>(departmentJpaRepository.findAllById(departmentIds));
        user.getDepartments().addAll(departments);
        userJpaRepository.save(user);
    }

    @Transactional
    public void removeDepartmentsFromUser(String userId, Set<String> departmentIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        user.getDepartments().removeIf(d -> departmentIds.contains(d.getId()));
        userJpaRepository.save(user);
    }

    @Transactional
    public void assignPositionsToUser(String userId, Set<String> positionIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        Set<PositionDbo> positions = new java.util.HashSet<>(positionJpaRepository.findAllById(positionIds));
        user.getPositions().addAll(positions);
        userJpaRepository.save(user);
    }

    @Transactional
    public void removePositionsFromUser(String userId, Set<String> positionIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        user.getPositions().removeIf(p -> positionIds.contains(p.getId()));
        userJpaRepository.save(user);
    }

    @Transactional
    public void assignRolesToUser(String userId, Set<String> roleIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        Set<RoleDbo> roles = new java.util.HashSet<>(roleJpaRepository.findAllById(roleIds));
        user.getRoles().addAll(roles);
        userJpaRepository.save(user);
    }

    @Transactional
    public void removeRolesFromUser(String userId, Set<String> roleIds) {
        UserDbo user = userJpaRepository.findById(userId).orElseThrow();
        user.getRoles().removeIf(r -> roleIds.contains(r.getId()));
        userJpaRepository.save(user);
    }
}

