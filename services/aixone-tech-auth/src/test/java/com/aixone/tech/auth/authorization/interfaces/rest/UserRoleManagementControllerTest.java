package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.command.AssignUserRoleCommand;
import com.aixone.tech.auth.authorization.application.dto.UserRoleResponse;
import com.aixone.tech.auth.authorization.application.service.UserRoleManagementApplicationService;
import com.aixone.tech.auth.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户角色管理控制器测试
 */
@WebMvcTest(UserRoleManagementController.class)
@Import(TestSecurityConfig.class)
class UserRoleManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleManagementApplicationService userRoleManagementApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String userId;
    private String roleId;
    private String userRoleId;
    private UserRoleResponse testUserRoleResponse;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        userId = "test-user";
        roleId = "test-role";
        userRoleId = UUID.randomUUID().toString();

        testUserRoleResponse = new UserRoleResponse(
            userRoleId,
            tenantId,
            userId,
            roleId,
            "Test Role",
            "Test role description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    void testAssignUserRole_Success() throws Exception {
        // Arrange
        AssignUserRoleCommand command = new AssignUserRoleCommand(tenantId, userId, roleId);

        when(userRoleManagementApplicationService.assignUserRole(any(AssignUserRoleCommand.class)))
            .thenReturn(testUserRoleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/users/{userId}/roles", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userRoleId").value(userRoleId))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.roleId").value(roleId))
                .andExpect(jsonPath("$.roleName").value("Test Role"))
                .andExpect(jsonPath("$.roleDescription").value("Test role description"));

        verify(userRoleManagementApplicationService).assignUserRole(any(AssignUserRoleCommand.class));
    }

    @Test
    void testAssignUserRole_ValidationError() throws Exception {
        // Arrange
        AssignUserRoleCommand command = new AssignUserRoleCommand(
            tenantId,
            "", // Empty userId should fail validation
            roleId
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/users/{userId}/roles", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());

        verify(userRoleManagementApplicationService, never()).assignUserRole(any(AssignUserRoleCommand.class));
    }

    @Test
    void testRemoveUserRole_Success() throws Exception {
        // Arrange
        doNothing().when(userRoleManagementApplicationService).removeUserRole(tenantId, userId, roleId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/users/{userId}/roles/{roleId}", userId, roleId)
                .param("tenantId", tenantId))
                .andExpect(status().isNoContent());

        verify(userRoleManagementApplicationService).removeUserRole(tenantId, userId, roleId);
    }

    @Test
    void testGetUserRoles_Success() throws Exception {
        // Arrange
        UserRoleResponse userRole1 = new UserRoleResponse(
            UUID.randomUUID().toString(),
            tenantId,
            userId,
            "role1",
            "Role 1",
            "Description 1",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        UserRoleResponse userRole2 = new UserRoleResponse(
            UUID.randomUUID().toString(),
            tenantId,
            userId,
            "role2",
            "Role 2",
            "Description 2",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        List<UserRoleResponse> userRoles = Arrays.asList(userRole1, userRole2);

        when(userRoleManagementApplicationService.getUserRoles(tenantId, userId))
            .thenReturn(userRoles);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/users/{userId}/roles", userId)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roleName").value("Role 1"))
                .andExpect(jsonPath("$[1].roleName").value("Role 2"));

        verify(userRoleManagementApplicationService).getUserRoles(tenantId, userId);
    }

    @Test
    void testGetUserRoles_EmptyList() throws Exception {
        // Arrange
        when(userRoleManagementApplicationService.getUserRoles(tenantId, userId))
            .thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/users/{userId}/roles", userId)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userRoleManagementApplicationService).getUserRoles(tenantId, userId);
    }

    @Test
    void testHasRole_ReturnsTrue() throws Exception {
        // Arrange
        when(userRoleManagementApplicationService.hasRole(tenantId, userId, roleId))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/users/{userId}/roles/{roleId}", userId, roleId)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userRoleManagementApplicationService).hasRole(tenantId, userId, roleId);
    }

    @Test
    void testHasRole_ReturnsFalse() throws Exception {
        // Arrange
        when(userRoleManagementApplicationService.hasRole(tenantId, userId, roleId))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/users/{userId}/roles/{roleId}", userId, roleId)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(userRoleManagementApplicationService).hasRole(tenantId, userId, roleId);
    }
}
