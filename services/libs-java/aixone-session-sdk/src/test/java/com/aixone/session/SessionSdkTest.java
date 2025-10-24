package com.aixone.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Session SDK 测试类
 */
@ExtendWith(MockitoExtension.class)
class SessionSdkTest {
    
    private TokenParser tokenParser;
    
    @BeforeEach
    void setUp() {
        tokenParser = new TokenParser("test-secret-key-for-jwt-token-generation-and-validation");
    }
    
    @Test
    void testSessionContext() {
        // 测试会话上下文管理
        SessionContext.SessionInfo sessionInfo = new SessionContext.SessionInfo("user123", "tenant1");
        sessionInfo.setClientId("client1");
        
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        sessionInfo.setRoles(roles);
        
        Set<String> permissions = new HashSet<>();
        permissions.add("user:read");
        permissions.add("user:write");
        sessionInfo.setPermissions(permissions);
        
        AbacAttributes abac = new AbacAttributes();
        abac.put("department", "IT");
        abac.put("position", "Manager");
        sessionInfo.setAbacAttributes(abac);
        
        // 设置会话上下文
        SessionContext.set(sessionInfo);
        
        // 验证会话信息
        assertTrue(SessionContext.hasSession());
        assertEquals("user123", SessionContext.getUserId());
        assertEquals("tenant1", SessionContext.getTenantId());
        assertEquals("client1", SessionContext.getClientId());
        assertEquals(roles, SessionContext.getRoles());
        assertEquals(permissions, SessionContext.getPermissions());
        assertEquals(abac, SessionContext.getAbacAttributes());
        
        // 测试便捷方法
        assertTrue(SessionContext.hasRole("ADMIN"));
        assertTrue(SessionContext.hasRole("USER"));
        assertFalse(SessionContext.hasRole("GUEST"));
        
        assertTrue(SessionContext.hasPermission("user:read"));
        assertTrue(SessionContext.hasPermission("user:write"));
        assertFalse(SessionContext.hasPermission("user:delete"));
        
        assertTrue(SessionContext.hasAnyRole("ADMIN", "GUEST"));
        assertTrue(SessionContext.hasAnyPermission("user:read", "admin:write"));
        
        assertEquals("IT", SessionContext.getAbacAttribute("department"));
        assertEquals("Manager", SessionContext.getAbacAttribute("position"));
        assertEquals("Default", SessionContext.getAbacAttribute("level", "Default"));
        
        // 清理会话上下文
        SessionContext.clear();
        assertFalse(SessionContext.hasSession());
        assertNull(SessionContext.getUserId());
    }
    
    @Test
    void testAbacAttributes() {
        AbacAttributes abac = new AbacAttributes();
        
        // 测试基本操作
        abac.put("key1", "value1");
        abac.put("key2", 123);
        abac.put("key3", true);
        
        assertEquals("value1", abac.get("key1"));
        assertEquals(123, abac.get("key2"));
        assertEquals(true, abac.get("key3"));
        assertNull(abac.get("nonexistent"));
        
        // 测试 Map 转换
        assertNotNull(abac.asMap());
        assertEquals(3, abac.asMap().size());
    }
    
    @Test
    void testSessionInfo() {
        SessionContext.SessionInfo sessionInfo = new SessionContext.SessionInfo("user123", "tenant1");
        sessionInfo.setTokenType("ACCESS");
        sessionInfo.setExpiresAt(new Date(System.currentTimeMillis() + 3600000)); // 1小时后过期
        
        // 测试基本属性
        assertTrue(sessionInfo.isAccessToken());
        assertFalse(sessionInfo.isRefreshToken());
        assertFalse(sessionInfo.isExpired());
        
        // 测试过期
        sessionInfo.setExpiresAt(new Date(System.currentTimeMillis() - 1000)); // 1秒前过期
        assertTrue(sessionInfo.isExpired());
        
        // 测试刷新令牌
        sessionInfo.setTokenType("REFRESH");
        assertFalse(sessionInfo.isAccessToken());
        assertTrue(sessionInfo.isRefreshToken());
    }
    
    @Test
    void testTokenParser() {
        // 注意：这里只是测试 TokenParser 的基本功能
        // 实际的 JWT 解析需要有效的 JWT token
        
        // 测试无效 token
        assertFalse(tokenParser.isValid("invalid-token"));
        assertNull(tokenParser.getUserId("invalid-token"));
        assertNull(tokenParser.getTenantId("invalid-token"));
    }
}
