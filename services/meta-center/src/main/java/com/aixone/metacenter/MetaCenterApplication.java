package com.aixone.metacenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 元数据服务主启动类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MetaCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetaCenterApplication.class, args);
    }
} 