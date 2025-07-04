package com.aixone.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController单元测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .param("username", "test")
                .param("password", "123456"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterNormal() throws Exception {
        String json = "{\"tenantId\":\"t1\",\"username\":\"user1\",\"password\":\"pwd123\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("X-Request-Id", "req-1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterRateLimit() throws Exception {
        String json = "{\"tenantId\":\"t1\",\"username\":\"user2\",\"password\":\"pwd123\"}";
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .header("X-Request-Id", "req-rl-" + i))
                    .andExpect(status().isOk());
        }
        // 第4次应被限流
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("X-Request-Id", "req-rl-4"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testRegisterIdempotent() throws Exception {
        String json = "{\"tenantId\":\"t1\",\"username\":\"user3\",\"password\":\"pwd123\"}";
        // 第一次请求
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("X-Request-Id", "req-idem-1"))
                .andExpect(status().isOk());
        // 重复请求
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("X-Request-Id", "req-idem-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSendSmsCode() throws Exception {
        mockMvc.perform(post("/api/v1/auth/sms/send")
                .param("phone", "13800000000"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendEmailCode() throws Exception {
        mockMvc.perform(post("/api/v1/auth/email/send")
                .param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRefreshToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", "mock-refresh-token"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAlipayCallback() throws Exception {
        mockMvc.perform(post("/api/v1/auth/alipay/callback")
                .param("code", "mock-code"))
                .andExpect(status().isOk());
    }

    @Test
    public void testWechatCallback() throws Exception {
        mockMvc.perform(post("/api/v1/auth/wechat/callback")
                .param("code", "mock-code"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAliyunCallback() throws Exception {
        mockMvc.perform(post("/api/v1/auth/aliyun/callback")
                .param("code", "mock-code"))
                .andExpect(status().isOk());
    }
} 