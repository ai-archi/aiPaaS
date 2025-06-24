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

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(String tenantId, UserDto.CreateUserCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        UUID tenantUuid = UUID.fromString(tenantId);
        Assert.isTrue(userRepository.findByTenantIdAndEmail(tenantUuid, command.getEmail()).isEmpty(), "Email already exists in this tenant");

        User user = User.createUser(tenantUuid, command.getEmail(), command.getPassword(), command.getUsername(), passwordEncoder);
        userRepository.save(user);
        return userDtoMapper.toDto(user);
    }

    @Transactional
    public void updateUserProfile(String tenantId, UUID userId, UserDto.UpdateProfileCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        User user = userRepository.findByTenantIdAndId(UUID.fromString(tenantId), userId)
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
    public void changePassword(String tenantId, UUID userId, UserDto.ChangePasswordCommand command) {
        User user = userRepository.findByTenantIdAndId(UUID.fromString(tenantId), userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.changePassword(command.getNewPassword(), passwordEncoder);
        userRepository.save(user);
    }

    public Optional<UserDto> getUser(String tenantId, UUID userId) {
        return userRepository.findByTenantIdAndId(UUID.fromString(tenantId), userId)
                .map(userDtoMapper::toDto);
    }

    public Optional<UserDto> getUserByEmail(String tenantId, String email) {
        return userRepository.findByTenantIdAndEmail(UUID.fromString(tenantId), email)
                .map(userDtoMapper::toDto);
    }

    public Optional<UserDto.UserPublicView> findUserById(String tenantId, UUID userId) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndId(UUID.fromString(tenantId), userId)
                .map(user -> UserDto.UserPublicView.builder()
                        .id(user.getId())
                        .username(user.getProfile().getUsername())
                        .avatarUrl(user.getProfile().getAvatarUrl())
                        .build());
    }

    public Optional<UserDto.UserCredentialsView> findUserCredentialsByEmail(String tenantId, String email) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndEmail(UUID.fromString(tenantId), email)
                .map(user -> UserDto.UserCredentialsView.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .hashedPassword(user.getHashedPassword())
                        .status(user.getStatus())
                        .build());
    }
}

