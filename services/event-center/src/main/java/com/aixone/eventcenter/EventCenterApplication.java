package com.aixone.eventcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 事件中心主应用入口
 * 启动 Spring Boot 应用
 */
@SpringBootApplication
public class EventCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventCenterApplication.class, args);
    }
}
