package com.aixone.directory.user.domain.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private final UUID id;
    private String tenantId;
    private String email;
    private String hashedPassword;
    private Profile profile;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User createUser(String tenantId, String email, String plainPassword, String username, PasswordEncoder passwordEncoder) {
        Assert.notNull(email, "Email cannot be null");
        Assert.notNull(plainPassword, "Password cannot be null");
        Assert.notNull(username, "Username cannot be null");
        Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");

        Profile initialProfile = Profile.builder().username(username).build();

        return User.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .email(email)
                .hashedPassword(passwordEncoder.encode(plainPassword))
                .profile(initialProfile)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateProfile(Profile newProfile) {
        Assert.notNull(newProfile, "New profile cannot be null");
        this.profile = this.profile.toBuilder()
            .username(newProfile.getUsername())
            .avatarUrl(newProfile.getAvatarUrl())
            .bio(newProfile.getBio())
            .build();
        this.touch();
    }

    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.hashedPassword);
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        Assert.state(this.status != UserStatus.SUSPENDED, "Cannot change password for a suspended user.");
        this.hashedPassword = passwordEncoder.encode(newPassword);
        this.touch();
    }

    public void suspend() {
        if (this.status == UserStatus.SUSPENDED) {
            return;
        }
        this.status = UserStatus.SUSPENDED;
        this.touch();
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            return;
        }
        this.status = UserStatus.ACTIVE;
        this.touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
} 