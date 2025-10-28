package com.aixone.workbench;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * AixOne工作台服务应用启动类
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class WorkbenchApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkbenchApplication.class, args);
    }
}
