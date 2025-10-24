package com.aixone.eventcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * 事件中心主应用入口
 * 启动 Spring Boot 应用
 */
@SpringBootApplication
public class EventCenterApplication {
    public static void main(String[] args) {
        // 如果没有指定 profile，默认使用 test profile
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", "test");
        }
        SpringApplication.run(EventCenterApplication.class, args);
    }
}
