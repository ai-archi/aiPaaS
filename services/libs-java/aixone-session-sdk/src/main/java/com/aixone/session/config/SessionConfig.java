package com.aixone.session.config;

import com.aixone.session.SessionInterceptor;
import com.aixone.session.TokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Session SDK 自动配置类
 * 提供 TokenParser 和 SessionInterceptor 的自动配置
 */
@Configuration
public class SessionConfig implements WebMvcConfigurer {
    
    @Value("${jwt.secret:aixone-tech-auth-secret-key-for-jwt-token-generation-and-validation}")
    private String jwtSecret;
    
    @Value("${jwt.issuer:aixone-tech-auth}")
    private String jwtIssuer;
    
    @Value("${multitenant.tenant-header:X-Tenant-ID}")
    private String tenantHeader;
    
    @Value("${session.require-auth:true}")
    private boolean requireAuth;
    
    @Value("${session.interceptor-patterns:/**}")
    private String[] interceptorPatterns;
    
    @Value("${session.exclude-patterns:}")
    private String[] excludePatterns;
    
    /**
     * 配置 TokenParser Bean
     */
    @Bean
    public TokenParser tokenParser() {
        return new TokenParser(jwtSecret, jwtIssuer);
    }
    
    /**
     * 配置 SessionInterceptor Bean
     */
    @Bean
    public SessionInterceptor sessionInterceptor(TokenParser tokenParser) {
        return new SessionInterceptor(tokenParser, requireAuth, tenantHeader);
    }
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor(tokenParser()))
                .addPathPatterns(interceptorPatterns)
                .excludePathPatterns(excludePatterns);
    }
}
