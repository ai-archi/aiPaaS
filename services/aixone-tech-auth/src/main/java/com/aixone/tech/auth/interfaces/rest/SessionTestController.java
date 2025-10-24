package com.aixone.tech.auth.interfaces.rest;

import com.aixone.common.session.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话测试控制器
 * 用于测试 session-sdk 的功能
 */
@RestController
@RequestMapping("/api/v1/session")
public class SessionTestController {
    
    /**
     * 获取当前会话信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo() {
        Map<String, Object> info = new HashMap<>();
        
        if (SessionContext.hasSession()) {
            SessionContext.SessionInfo sessionInfo = SessionContext.get();
            info.put("userId", sessionInfo.getUserId());
            info.put("tenantId", sessionInfo.getTenantId());
            info.put("clientId", sessionInfo.getClientId());
            info.put("tokenType", sessionInfo.getTokenType());
            info.put("abacAttributes", sessionInfo.getAbacAttributes() != null ? sessionInfo.getAbacAttributes().asMap() : null);
            info.put("expired", sessionInfo.isExpired());
        } else {
            info.put("message", "No active session");
        }
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * 测试基础会话信息
     */
    @GetMapping("/test-basic")
    public ResponseEntity<Map<String, Object>> testBasic() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Basic session test");
        result.put("userId", SessionContext.getUserId());
        result.put("tenantId", SessionContext.getTenantId());
        result.put("clientId", SessionContext.getClientId());
        result.put("tokenType", SessionContext.getTokenType());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试租户信息
     */
    @GetMapping("/test-tenant")
    public ResponseEntity<Map<String, Object>> testTenant() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Tenant test - check tenant information");
        result.put("userId", SessionContext.getUserId());
        result.put("tenantId", SessionContext.getTenantId());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试 ABAC 属性
     */
    @GetMapping("/test-abac")
    public ResponseEntity<Map<String, Object>> testAbac() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "ABAC test");
        result.put("userId", SessionContext.getUserId());
        result.put("department", SessionContext.getAbacAttribute("department"));
        result.put("position", SessionContext.getAbacAttribute("position"));
        result.put("level", SessionContext.getAbacAttribute("level"));
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试便捷方法
     */
    @GetMapping("/test-convenience")
    public ResponseEntity<Map<String, Object>> testConvenience() {
        Map<String, Object> result = new HashMap<>();
        result.put("hasSession", SessionContext.hasSession());
        result.put("userId", SessionContext.getUserId());
        result.put("tenantId", SessionContext.getTenantId());
        result.put("clientId", SessionContext.getClientId());
        result.put("tokenType", SessionContext.getTokenType());
        return ResponseEntity.ok(result);
    }
}
