package com.aixone.tech.auth;

import com.aixone.tech.auth.config.TestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 认证授权服务启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@Import(TestConfig.class)
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
