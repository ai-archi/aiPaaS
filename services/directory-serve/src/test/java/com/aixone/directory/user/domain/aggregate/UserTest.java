package com.aixone.directory.user.domain.aggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User 领域聚合根的单元测试
 */
public class UserTest {
    private PasswordEncoder passwordEncoder;
    private UUID tenantId;
    private String email;
    private String plainPassword;
    private String username;

    @BeforeEach
    public void setUp() {
        System.out.println("[DEBUG] setUp email=" + email);
        System.out.println("[DEBUG] setUp tenantId=" + tenantId);
        System.out.println("[DEBUG] setUp plainPassword=" + plainPassword);
        System.out.println("[DEBUG] setUp username=" + username);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        tenantId = UUID.randomUUID();
        email = "test@example.com";
        plainPassword = "password123";
        username = "testuser";
        Mockito.when(passwordEncoder.encode(plainPassword)).thenReturn("hashedPassword");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    }

    @Test
    public void testCreateUser() {
        System.out.println("[DEBUG] testCreateUser email=" + email);
        System.out.println("[DEBUG] testCreateUser tenantId=" + tenantId);
        System.out.println("[DEBUG] testCreateUser plainPassword=" + plainPassword);
        System.out.println("[DEBUG] testCreateUser username=" + username);
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals(username, user.getProfile().getUsername());
        assertTrue(user.checkPassword(plainPassword, passwordEncoder));
    }

    @Test
    public void testUpdateProfile() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        Profile newProfile = Profile.builder().username("newuser").avatarUrl("avatar.png").bio("bio").build();
        user.updateProfile(newProfile);
        assertEquals("newuser", user.getProfile().getUsername());
        assertEquals("avatar.png", user.getProfile().getAvatarUrl());
        assertEquals("bio", user.getProfile().getBio());
    }

    @Test
    public void testChangePassword() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        String newPassword = "newPassword123";
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        user.changePassword(newPassword, passwordEncoder);
        assertTrue(user.checkPassword(newPassword, passwordEncoder));
    }

    @Test
    public void testSuspendAndActivate() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        user.suspend();
        assertEquals(UserStatus.SUSPENDED, user.getStatus());
        user.activate();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    public void testAssignAndRemoveGroup() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        UUID groupId = UUID.randomUUID();
        user.assignToGroup(groupId);
        assertTrue(user.getGroupIds().contains(groupId));
        user.removeFromGroup(groupId);
        assertFalse(user.getGroupIds().contains(groupId));
    }

    @Test
    public void testGrantAndRevokeRole() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        UUID roleId = UUID.randomUUID();
        user.grantRole(roleId);
        assertTrue(user.getRoleIds().contains(roleId));
        user.revokeRole(roleId);
        assertFalse(user.getRoleIds().contains(roleId));
    }

    @Test
    public void testChangePasswordWhenSuspendedThrowsException() {
        User user = User.createUser(tenantId, email, plainPassword, username, passwordEncoder);
        user.suspend();
        assertThrows(IllegalStateException.class, () -> user.changePassword("anotherPassword", passwordEncoder));
    }
}
