package com.aixone.directory.config;

import com.aixone.common.session.SessionInterceptor;
import com.aixone.common.session.TokenParser;
import com.aixone.permission.provider.UserPermissionProvider;
import com.aixone.permission.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Primary;
import com.aixone.common.session.SessionContext;

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
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/actuator/health",
                    "/api/v1/tenants/**",
                    "/api/v1/tenant-groups/**",
                    "/api/v1/menus/**"
                );
    }

    /**
     * 提供给权限包的用户上下文适配器，自动获取当前登录用户信息
     * 注释掉以避免编译错误，待权限 SDK 稳定后再启用
     */
    // @Bean
    // @Primary
    // public UserPermissionProvider userPermissionProvider() {
    //     return new UserPermissionProvider() {
    //         @Override
    //         public java.util.List<com.aixone.permission.model.Permission> getPermissions(com.aixone.permission.model.User user) {
    //             // TODO: 可根据 user.getUserId() 查询权限，或从 SessionContext 获取
    //             return java.util.Collections.emptyList();
    //         }
    //
    //         @Override
    //         public java.util.List<com.aixone.permission.model.Role> getUserRoles(String userId, String tenantId) {
    //             // TODO: 根据用户ID和租户ID查询用户角色
    //             return java.util.Collections.emptyList();
    //         }
    //     };
    // }
} 