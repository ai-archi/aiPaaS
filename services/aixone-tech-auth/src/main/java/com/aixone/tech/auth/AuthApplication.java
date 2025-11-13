package com.aixone.tech.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 认证服务启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(
    basePackages = {"com.aixone.tech.auth", "com.aixone.common", "com.aixone.audit"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = com.aixone.common.session.config.SessionConfig.class
    )
)
@EnableJpaRepositories(basePackages = {"com.aixone.tech.auth", "com.aixone.audit.infrastructure"})
@EntityScan(basePackages = {"com.aixone.tech.auth", "com.aixone.audit.infrastructure"})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
