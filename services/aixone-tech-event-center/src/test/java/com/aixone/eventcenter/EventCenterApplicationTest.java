package com.aixone.eventcenter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * EventCenterApplication 集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("EventCenterApplication 集成测试")
class EventCenterApplicationTest {

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("应该成功启动应用程序")
    void shouldStartApplicationSuccessfully() {
        // 这个测试验证Spring Boot应用程序能够成功启动
        // 如果应用程序配置有问题，这个测试会失败
        assert true; // 如果到达这里，说明应用程序启动成功
    }
}
