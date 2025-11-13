package com.aixone.tech.auth.config;

import com.aixone.common.session.SessionInterceptor;
import com.aixone.common.session.TokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auth服务的Session拦截器配置
 * 排除公开接口，避免在登录接口上要求Token
 * 使用 @Primary 和 @ConditionalOnMissingBean 来覆盖SDK的自动配置
 */
@Configuration
public class AuthSessionConfig implements WebMvcConfigurer {
    
    @Value("${jwt.secret:aixone-tech-auth-secret-key-for-jwt-token-generation-and-validation}")
    private String jwtSecret;
    
    @Value("${jwt.issuer:aixone-tech-auth}")
    private String jwtIssuer;
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public TokenParser authTokenParser() {
        return new TokenParser(jwtSecret, jwtIssuer);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SessionInterceptor authSessionInterceptor(TokenParser tokenParser) {
        // 创建不需要认证的拦截器（对于排除的路径）
        return SessionInterceptor.createOptional(tokenParser);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TokenParser tokenParser = authTokenParser();
        SessionInterceptor interceptor = SessionInterceptor.createOptional(tokenParser);
        
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    // 公开的认证接口（相对于 context-path /api/v1）
                    "/auth/login",
                    "/auth/refresh",
                    "/auth/logout",
                    "/auth/validate",
                    "/auth/sms/**",
                    "/auth/email/**",
                    "/verification-codes/**"
                );
    }
}

