package com.aixone.directory.config;

import com.aixone.session.SessionInterceptor;
import com.aixone.session.TokenParser;
import com.aixone.permission.provider.UserPermissionProvider;
import com.aixone.permission.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Primary;
import com.aixone.session.SessionContext;

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

    /**
     * 提供给权限包的用户上下文适配器，自动获取当前登录用户信息
     */
    @Bean
    @Primary
    public UserPermissionProvider userPermissionProvider() {
        return user -> {
            // TODO: 可根据 user.getUserId() 查询权限，或从 SessionContext 获取
            return java.util.Collections.emptyList();
        };
    }
} 