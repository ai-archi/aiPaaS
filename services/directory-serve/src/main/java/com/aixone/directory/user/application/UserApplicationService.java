package com.aixone.directory.user.application;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.aixone.directory.user.domain.aggregate.Profile;
import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.domain.aggregate.UserStatus;
import com.aixone.directory.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Command Handlers ---

    public UUID createUser(String tenantId, UserDto.CreateUserCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        Assert.isTrue(userRepository.findByTenantIdAndEmail(tenantId, command.getEmail()).isEmpty(), "Email already exists in this tenant");

        User user = User.createUser(
                tenantId,
                command.getEmail(),
                command.getPassword(),
                command.getUsername(),
                passwordEncoder
        );
        userRepository.save(user);
        // Here you would typically publish a UserRegisteredEvent(tenantId, user.getId())
        return user.getId();
    }

    public void updateUserProfile(String tenantId, UUID userId, UserDto.UpdateProfileCommand command) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        User user = userRepository.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found in this tenant"));

        Profile newProfile = user.getProfile().toBuilder()
                .username(command.getUsername())
                .avatarUrl(command.getAvatarUrl())
                .bio(command.getBio())
                .build();

        user.updateProfile(newProfile);
        userRepository.save(user);
        // Here you would typically publish a UserProfileUpdatedEvent(tenantId, user.getId())
    }

    // --- Query Handlers ---

    public Optional<UserDto.UserPublicView> findUserById(String tenantId, UUID userId) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndId(tenantId, userId)
                .map(user -> UserDto.UserPublicView.builder()
                        .id(user.getId())
                        .username(user.getProfile().getUsername())
                        .avatarUrl(user.getProfile().getAvatarUrl())
                        .createdAt(user.getCreatedAt())
                        .build());
    }

    public Optional<UserDto.UserCredentialsView> findUserCredentialsByEmail(String tenantId, String email) {
        Assert.notNull(tenantId, "TenantId cannot be null");
        return userRepository.findByTenantIdAndEmail(tenantId, email)
                .map(user -> UserDto.UserCredentialsView.builder()
                        .id(user.getId())
                        .hashedPassword(user.getHashedPassword())
                        .isActive(user.getStatus() == UserStatus.ACTIVE)
                        .build());
    }
} 