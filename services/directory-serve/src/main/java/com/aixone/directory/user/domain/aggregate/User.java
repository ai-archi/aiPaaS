package com.aixone.directory.user.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final Logger log = LoggerFactory.getLogger(User.class);

    private String id;
    private String tenantId;
    private String email;
    private String hashedPassword;
    private Profile profile;
    private UserStatus status;
    private Set<String> roleIds = new HashSet<>();
    private Set<String> groupIds = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creating a new User
    private User(String tenantId, String email, String hashedPassword, Profile profile) {
        this.id = UUID.randomUUID().toString();
        this.tenantId = tenantId;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profile = profile;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User createUser(String tenantId, String email, String plainPassword, String username, PasswordEncoder passwordEncoder) {
        log.debug("createUser 入参: tenantId={}, email={}, plainPassword={}, username={}, passwordEncoder={}", tenantId, email, plainPassword, username, passwordEncoder != null);
        Assert.hasText(email, "Email cannot be null or empty");
        Assert.hasText(plainPassword, "Password cannot be null or empty");
        Assert.hasText(username, "Username cannot be null or empty");
        Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        // 验证密码长度
        if (plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        Profile initialProfile = Profile.builder().username(username).build();
        User user = new User(
                tenantId,
                email,
                passwordEncoder.encode(plainPassword),
                initialProfile
        );
        log.debug("createUser 返回: user={}", user);
        return user;
    }

    public void updateProfile(Profile newProfile) {
        Assert.notNull(newProfile, "New profile cannot be null");
        Assert.hasText(newProfile.getUsername(), "Profile username cannot be null or empty");
        this.profile = this.profile.updateWith(newProfile);
        this.touch();
    }

    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.hashedPassword);
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        Assert.state(this.status != UserStatus.SUSPENDED, "Cannot change password for a suspended user.");
        Assert.hasText(newPassword, "New password cannot be null or empty");
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.hashedPassword = passwordEncoder.encode(newPassword);
        this.touch();
    }

    public void suspend() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new IllegalStateException("User is already suspended");
        }
        this.status = UserStatus.SUSPENDED;
        this.touch();
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            throw new IllegalStateException("User is already active");
        }
        this.status = UserStatus.ACTIVE;
        this.touch();
    }

    public void assignToGroup(String groupId) {
        Assert.notNull(groupId, "GroupId cannot be null");
        this.groupIds.add(groupId);
        this.touch();
    }

    public void removeFromGroup(String groupId) {
        Assert.notNull(groupId, "GroupId cannot be null");
        this.groupIds.remove(groupId);
        this.touch();
    }

    public void grantRole(String roleId) {
        Assert.notNull(roleId, "RoleId cannot be null");
        this.roleIds.add(roleId);
        this.touch();
    }

    public void revokeRole(String roleId) {
        Assert.notNull(roleId, "RoleId cannot be null");
        this.roleIds.remove(roleId);
        this.touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
} 