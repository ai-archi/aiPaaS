package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.tech.auth.authentication.application.dto.auth.LoginRequest;
import com.aixone.tech.auth.config.TestDataConfig;
import com.aixone.tech.auth.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@SpringBootTest(webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestDataConfig.class})
class AuthenticationControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLogin() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setTenantId("test-tenant");
        request.setUsername("test-user");
        request.setPassword("test-password");
        request.setClientId("test-client");
        request.setClientSecret("test-secret");

        // When & Then
        // 注意：在MockMvc中，context-path需要手动处理，使用不带context-path的路径
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").exists())
                .andExpect(jsonPath("$.data.scope").exists())
                .andExpect(jsonPath("$.data.tenantId").value("test-tenant"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setTenantId("test-tenant");
        request.setUsername("invalid-user");
        request.setPassword("invalid-password");
        request.setClientId("invalid-client"); // 使用无效的客户端ID
        request.setClientSecret("invalid-secret");

        // When & Then
        // 注意：在MockMvc中，context-path需要手动处理，使用不带context-path的路径
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 现在返回200，错误在data中
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void testRefreshToken() throws Exception {
        // Given
        String refreshTokenRequest = """
            {
                "tenantId": "test-tenant",
                "refreshToken": "test-refresh-token",
                "clientId": "test-client",
                "clientSecret": "test-secret"
            }
            """;

        // When & Then
        // 注意：在MockMvc中，context-path需要手动处理，使用不带context-path的路径
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer")); // TokenResponse直接返回，不是包装的
    }

    @Test
    void testValidateToken() throws Exception {
        // Given
        String token = "Bearer test-token";
        String tenantId = "test-tenant";

        // When & Then
        // 注意：在MockMvc中，context-path需要手动处理，使用不带context-path的路径
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", token)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(content().string("false")); // 因为测试环境中没有真实的令牌，返回boolean
    }
}
