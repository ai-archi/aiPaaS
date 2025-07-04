package com.aixone.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TenantRegisterController单元测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TenantRegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRegisterEnabled() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/test-tenant/register-enabled"))
                .andExpect(status().isOk());
    }
} 