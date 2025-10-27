package com.aixone.tech.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.aixone.audit.application.AuditService;

/**
 * 测试配置
 */
@Configuration
@Profile("dev")
public class TestConfig {

    @Bean
    @Primary
    public AuditService auditService() {
        // 创建一个简单的 mock 实现
        return new AuditService(null, null) {
            @Override
            public com.aixone.audit.domain.AuditLog logSuccess(String action, String resource, java.util.Map<String, Object> details) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logSuccess(String action, String resource) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logFailure(String action, String resource, String errorMessage) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logFailure(String action, String resource, String errorMessage, java.util.Map<String, Object> details) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logAction(String action, String resource, String result, 
                                                             java.util.Map<String, Object> details, String errorMessage) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logLoginSuccess(String userId, String clientIp, String userAgent) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logLoginFailure(String userId, String reason, String clientIp, String userAgent) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logLogout(String userId, String clientIp, String userAgent) {
                return null; // 空实现，用于测试
            }
            
            @Override
            public com.aixone.audit.domain.AuditLog logPermissionCheck(String userId, String resource, String action, 
                                                                      String result, String reason) {
                return null; // 空实现，用于测试
            }
        };
    }
}
