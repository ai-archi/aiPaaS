package com.aixone.directory.user.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private UUID tenantId;
    private String email;
    private String hashedPassword;
    private Profile profile;
    private UserStatus status;
    private Set<UUID> roleIds = new HashSet<>();
    private Set<UUID> groupIds = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creating a new User
    private User(UUID tenantId, String email, String hashedPassword, Profile profile) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profile = profile;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User createUser(UUID tenantId, String email, String plainPassword, String username, PasswordEncoder passwordEncoder) {
        System.out.println("[DEBUG] createUser email=" + email);
        Assert.notNull(email, "Email cannot be null");
        Assert.notNull(plainPassword, "Password cannot be null");
        Assert.notNull(username, "Username cannot be null");
        Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");

        Profile initialProfile = Profile.builder().username(username).build();

        return new User(
                tenantId,
                email,
                passwordEncoder.encode(plainPassword),
                initialProfile
        );
    }

    public void updateProfile(Profile newProfile) {
        Assert.notNull(newProfile, "New profile cannot be null");
        this.profile = this.profile.updateWith(newProfile);
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

    public void assignToGroup(UUID groupId) {
        this.groupIds.add(groupId);
        this.touch();
    }

    public void removeFromGroup(UUID groupId) {
        this.groupIds.remove(groupId);
        this.touch();
    }

    public void grantRole(UUID roleId) {
        this.roleIds.add(roleId);
        this.touch();
    }

    public void revokeRole(UUID roleId) {
        this.roleIds.remove(roleId);
        this.touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
} 