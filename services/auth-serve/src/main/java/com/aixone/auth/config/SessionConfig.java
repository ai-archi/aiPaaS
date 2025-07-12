package com.aixone.auth.config;

import com.aixone.session.SessionInterceptor;
import com.aixone.session.TokenParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 SessionInterceptor，实现 Token/租户/权限上下文自动注入
 */
@Configuration
public class SessionConfig implements WebMvcConfigurer {
    @Value("${security.jwt.secret:secret}")
    private String jwtSecret;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor(new TokenParser(jwtSecret)))
                .addPathPatterns("/**");
    }
} 