package com.aiplatform.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))
                // 用户服务路由
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))
                // Java智能体服务路由
                .route("java-agent", r -> r.path("/java-agent/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://java-agent"))
                // Python智能体服务路由
                .route("python-agent", r -> r.path("/python-agent/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://python-agent"))
                .build();
    }
} 