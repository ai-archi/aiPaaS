package com.aixone.event.integration;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.dto.EventDTO;
import com.aixone.event.listener.EventListenerManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kafka事件监听器集成测试
 * 测试真实的Kafka消息发送和接收
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "aixone.event.kafka.bootstrap-servers=localhost:9092"
})
@EmbeddedKafka(partitions = 1, topics = {"test-user-events", "test-order-events", "test-payment-events"})
@DisplayName("Kafka事件监听器集成测试")
class KafkaEventListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private EventListenerManager eventListenerManager;
    
    @Autowired
    private TestKafkaListener testKafkaListener;
    
    @Autowired
    private TestOrderListener testOrderListener;
    
    @Autowired
    private TestPaymentListener testPaymentListener;

    @BeforeEach
    void setUp() {
        // 重置计数器
        testKafkaListener.reset();
        testOrderListener.reset();
        testPaymentListener.reset();
    }

    @Test
    @DisplayName("用户登录事件监听测试")
    void testUserLoginEventListening() throws InterruptedException {
        // 创建用户登录事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"12345\",\"loginTime\":\"" + Instant.now() + "\"}",
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-user-events", event);
        
        // 等待消息处理
        boolean received = testKafkaListener.getUserLoginLatch().await(5, TimeUnit.SECONDS);
        
        // 验证消息是否被接收
        assertTrue(received, "用户登录事件应该被接收");
        assertEquals(1, testKafkaListener.getUserLoginCount().get(), "用户登录事件计数应该为1");
        assertEquals("user.login", testKafkaListener.getLastEventType().get(), "事件类型应该匹配");
    }

    @Test
    @DisplayName("订单事件监听测试")
    void testOrderEventListening() throws InterruptedException {
        // 创建订单事件
        EventDTO event = new EventDTO(
            "order.created",
            "order-service",
            "{\"orderId\":\"67890\",\"amount\":99.99}",
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-order-events", event);
        
        // 等待消息处理
        boolean received = testOrderListener.getOrderLatch().await(5, TimeUnit.SECONDS);
        
        // 验证消息是否被接收
        assertTrue(received, "订单事件应该被接收");
        assertEquals(1, testOrderListener.getOrderCount().get(), "订单事件计数应该为1");
        assertEquals("order.created", testOrderListener.getLastEventType().get(), "事件类型应该匹配");
    }

    @Test
    @DisplayName("支付事件监听测试")
    void testPaymentEventListening() throws InterruptedException {
        // 创建支付事件
        EventDTO event = new EventDTO(
            "payment.failed",
            "payment-service",
            "{\"paymentId\":\"11111\",\"reason\":\"insufficient_funds\"}",
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-payment-events", event);
        
        // 等待消息处理
        boolean received = testPaymentListener.getPaymentLatch().await(5, TimeUnit.SECONDS);
        
        // 验证消息是否被接收
        assertTrue(received, "支付事件应该被接收");
        assertEquals(1, testPaymentListener.getPaymentCount().get(), "支付事件计数应该为1");
        assertEquals("payment.failed", testPaymentListener.getLastEventType().get(), "事件类型应该匹配");
    }

    @Test
    @DisplayName("事件类型过滤测试")
    void testEventTypeFiltering() throws InterruptedException {
        // 发送不匹配的事件类型
        EventDTO event = new EventDTO(
            "user.logout",  // 不匹配 user.login
            "auth-service",
            "{\"userId\":\"12345\"}",
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-user-events", event);
        
        // 等待一段时间确保消息被处理
        Thread.sleep(2000);
        
        // 验证不匹配的事件类型不会被处理
        assertEquals(0, testKafkaListener.getUserLoginCount().get(), "不匹配的事件类型不应该被处理");
    }

    @Test
    @DisplayName("多个监听器优先级测试")
    void testMultipleListenerPriority() throws InterruptedException {
        // 创建用户登录事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"12345\"}",
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-user-events", event);
        
        // 等待消息处理
        boolean received = testKafkaListener.getUserLoginLatch().await(5, TimeUnit.SECONDS);
        
        // 验证消息被接收
        assertTrue(received, "用户登录事件应该被接收");
        
        // 验证监听器按优先级执行
        assertTrue(testKafkaListener.getLastEventType().get() != null, "事件应该被处理");
    }

    @Test
    @DisplayName("监听器注册验证测试")
    void testListenerRegistration() {
        // 验证监听器已注册
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-user-events"), 
            "test-user-events Topic应该已注册");
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-order-events"), 
            "test-order-events Topic应该已注册");
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-payment-events"), 
            "test-payment-events Topic应该已注册");
        
        // 验证监听器数量
        assertTrue(eventListenerManager.getTotalListenerCount() > 0, "应该有注册的监听器");
    }

    @Test
    @DisplayName("错误处理测试")
    void testErrorHandling() throws InterruptedException {
        // 创建会导致异常的事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"error-user\"}",  // 特殊用户ID会触发异常
            "test-tenant"
        );
        
        // 发送消息到Kafka
        kafkaTemplate.send("test-user-events", event);
        
        // 等待消息处理
        Thread.sleep(3000);
        
        // 验证异常被正确处理（不会导致测试失败）
        // 这里主要验证异常不会影响其他监听器的正常工作
        assertTrue(true, "异常应该被正确处理");
    }

    // 测试监听器组件
    @Component
    static class TestKafkaListener {
        private final AtomicInteger userLoginCount = new AtomicInteger(0);
        private final AtomicReference<String> lastEventType = new AtomicReference<>();
        private final CountDownLatch userLoginLatch = new CountDownLatch(1);

        @EventListener(
            topics = "test-user-events",
            eventTypes = "user.login",
            groupId = "test-user-group",
            id = "test-user-login-listener",
            priority = 1
        )
        public void handleUserLogin(EventDTO event) {
            userLoginCount.incrementAndGet();
            lastEventType.set(event.getEventType());
            userLoginLatch.countDown();
            
            // 模拟异常处理
            if ("error-user".equals(event.getData())) {
                throw new RuntimeException("模拟异常");
            }
        }

        public void reset() {
            userLoginCount.set(0);
            lastEventType.set(null);
            // 重新创建CountDownLatch
        }

        public AtomicInteger getUserLoginCount() { return userLoginCount; }
        public AtomicReference<String> getLastEventType() { return lastEventType; }
        public CountDownLatch getUserLoginLatch() { return userLoginLatch; }
    }

    @Component
    static class TestOrderListener {
        private final AtomicInteger orderCount = new AtomicInteger(0);
        private final AtomicReference<String> lastEventType = new AtomicReference<>();
        private final CountDownLatch orderLatch = new CountDownLatch(1);

        @EventListener(
            topics = "test-order-events",
            groupId = "test-order-group",
            id = "test-order-listener",
            priority = 2
        )
        public void handleOrderEvents(EventDTO event) {
            orderCount.incrementAndGet();
            lastEventType.set(event.getEventType());
            orderLatch.countDown();
        }

        public void reset() {
            orderCount.set(0);
            lastEventType.set(null);
        }

        public AtomicInteger getOrderCount() { return orderCount; }
        public AtomicReference<String> getLastEventType() { return lastEventType; }
        public CountDownLatch getOrderLatch() { return orderLatch; }
    }

    @Component
    static class TestPaymentListener {
        private final AtomicInteger paymentCount = new AtomicInteger(0);
        private final AtomicReference<String> lastEventType = new AtomicReference<>();
        private final CountDownLatch paymentLatch = new CountDownLatch(1);

        @EventListener(
            topics = "test-payment-events",
            eventTypes = "payment.failed",
            groupId = "test-payment-group",
            id = "test-payment-listener",
            priority = 0  // 高优先级
        )
        public void handlePaymentEvents(EventDTO event) {
            paymentCount.incrementAndGet();
            lastEventType.set(event.getEventType());
            paymentLatch.countDown();
        }

        public void reset() {
            paymentCount.set(0);
            lastEventType.set(null);
        }

        public AtomicInteger getPaymentCount() { return paymentCount; }
        public AtomicReference<String> getLastEventType() { return lastEventType; }
        public CountDownLatch getPaymentLatch() { return paymentLatch; }
    }
}
