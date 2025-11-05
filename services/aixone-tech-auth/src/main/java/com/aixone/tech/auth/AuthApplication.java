package com.aixone.tech.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 认证服务启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.aixone.tech.auth", "com.aixone.common"})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
