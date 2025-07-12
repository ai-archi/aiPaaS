package com.aixone.permission.config;

import com.aixone.permission.filter.PermissionFilter;
import com.aixone.permission.provider.DefaultUserPermissionProvider;
import com.aixone.permission.provider.UserPermissionProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import jakarta.servlet.Filter;

/**
 * 权限中台自动配置
 * 通过@EnablePermission自动装配相关组件
 */
@Configuration
public class PermissionAutoConfiguration {
    /**
     * 注册权限上下文过滤器，自动注入到Servlet过滤器链
     */
    @Bean
    public FilterRegistrationBean<Filter> permissionFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PermissionFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(10);
        return registration;
    }

    /**
     * 默认用户权限提供者，业务可自定义覆盖
     */
    @Bean
    @ConditionalOnMissingBean(UserPermissionProvider.class)
    public UserPermissionProvider userPermissionProvider() {
        return new DefaultUserPermissionProvider();
    }
} 