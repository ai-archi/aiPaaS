package com.aixone.directory.user.application;

import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;
import com.aixone.directory.role.infrastructure.persistence.dbo.RoleDbo;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentJpaRepository;
import com.aixone.directory.organization.infrastructure.persistence.PositionJpaRepository;
import com.aixone.directory.role.infrastructure.persistence.RoleJpaRepository;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
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

