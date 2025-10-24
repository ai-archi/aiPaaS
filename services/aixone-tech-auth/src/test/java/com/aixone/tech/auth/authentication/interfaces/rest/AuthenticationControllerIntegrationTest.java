package com.aixone.tech.auth.authentication.interfaces.rest;

import com.aixone.tech.auth.authentication.application.dto.auth.LoginRequest;
import com.aixone.tech.auth.config.TestDataConfig;
import com.aixone.tech.auth.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestDataConfig.class})
class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void testLogin() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        LoginRequest request = new LoginRequest();
        request.setTenantId("test-tenant");
        request.setUsername("test-user");
        request.setPassword("test-password");
        request.setClientId("test-client");
        request.setClientSecret("test-secret");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.scope").exists())
                .andExpect(jsonPath("$.tenantId").value("test-tenant"))
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        LoginRequest request = new LoginRequest();
        request.setTenantId("test-tenant");
        request.setUsername("invalid-user");
        request.setPassword("invalid-password");
        request.setClientId("invalid-client"); // 使用无效的客户端ID
        request.setClientSecret("invalid-secret");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("客户端不存在"));
    }

    @Test
    void testRefreshToken() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        String refreshTokenRequest = """
            {
                "tenantId": "test-tenant",
                "refreshToken": "test-refresh-token",
                "clientId": "test-client",
                "clientSecret": "test-secret"
            }
            """;

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void testValidateToken() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        String token = "Bearer test-token";
        String tenantId = "test-tenant";

        // When & Then
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", token)
                .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(content().string("false")); // 因为测试环境中没有真实的令牌
    }
}
