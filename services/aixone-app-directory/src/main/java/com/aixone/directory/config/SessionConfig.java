package com.aixone.directory.config;

import com.aixone.common.session.SessionInterceptor;
import com.aixone.common.session.TokenParser;
import com.aixone.directory.permission.interfaces.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 SessionInterceptor 和 PermissionInterceptor
 * 实现 Token/租户/权限上下文自动注入和权限验证
 */
@Configuration
public class SessionConfig implements WebMvcConfigurer {
    @Value("${jwt.secret:aixone-tech-auth-secret-key-for-jwt-token-generation-and-validation}")
    private String jwtSecret;
    
    private final PermissionInterceptor permissionInterceptor;

    public SessionConfig(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 SessionInterceptor（优先级高，先执行）
        // 使用 createOptional 创建拦截器，即使没有 token 也不会直接返回 401
        // 由各个 Controller 自行检查 tenantId 并返回适当的错误码
        // 使用与 Auth 服务相同的 issuer
        TokenParser tokenParser = new TokenParser(jwtSecret, "aixone-tech-auth");
        registry.addInterceptor(SessionInterceptor.createOptional(tokenParser))
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/actuator/health",
                    "/api/v1/tenants/**",
                    "/api/v1/tenant-groups/**"
                    // 注意：菜单接口不再排除，需要从token获取tenantId
                )
                .order(1);  // 设置优先级，先执行
        
        // 注册 PermissionInterceptor（优先级低，后执行）
        // 拦截管理接口（/api/v1/admin/**）进行权限验证
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns(
                    "/actuator/**"
                )
                .order(2);  // 设置优先级，后执行
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