package com.aixone.event;

import com.aixone.event.config.EventAutoConfiguration;
import com.aixone.event.config.TestKafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 测试应用启动类
 * 用于集成测试的Spring Boot应用
 */
@SpringBootApplication
@Import({EventAutoConfiguration.class, TestKafkaConfiguration.class})
public class TestApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
