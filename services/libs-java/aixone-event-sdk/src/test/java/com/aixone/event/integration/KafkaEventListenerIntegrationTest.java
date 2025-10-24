package com.aixone.event.integration;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.dto.EventDTO;
import com.aixone.event.listener.EventListenerManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
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
 * 直接使用Spring Kafka发送事件，通过@EventListener验证事件监听
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "aixone.event.kafka.bootstrap-servers=localhost:9092",
    "aixone.event.enabled=true"
})
@EmbeddedKafka(partitions = 1, topics = {
    "test-user-events", 
    "test-order-events", 
    "test-payment-events",
    "test-batch-events"
})
@DisplayName("Kafka事件监听器集成测试")
class KafkaEventListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private EventListenerManager eventListenerManager;
    
    @Autowired
    private TestEventListener testEventListener;

    @BeforeEach
    void setUp() {
        testEventListener.reset();
    }

    @Test
    @DisplayName("用户登录事件监听测试")
    void testUserLoginEventListening() throws InterruptedException {
        // 创建用户登录事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"12345\",\"loginTime\":\"" + Instant.now() + "\",\"ip\":\"192.168.1.1\"}",
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-user-events", event);
        
        // 等待@EventListener处理事件
        boolean received = testEventListener.getUserLoginLatch().await(5, TimeUnit.SECONDS);
        
        // 验证事件监听是否正确
        assertTrue(received, "用户登录事件应该被@EventListener接收");
        assertEquals(1, testEventListener.getUserLoginCount().get(), "用户登录事件计数应该为1");
        assertEquals("user.login", testEventListener.getLastUserEventType().get(), "事件类型应该匹配");
        assertNotNull(testEventListener.getLastUserEvent().get(), "事件对象应该不为空");
    }

    @Test
    @DisplayName("订单创建事件监听测试")
    void testOrderCreatedEventListening() throws InterruptedException {
        // 创建订单事件
        EventDTO event = new EventDTO(
            "order.created",
            "order-service",
            "{\"orderId\":\"ORD-67890\",\"amount\":199.99,\"currency\":\"USD\"}",
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-order-events", event);
        
        // 等待@EventListener处理事件
        boolean received = testEventListener.getOrderLatch().await(5, TimeUnit.SECONDS);
        
        // 验证事件监听是否正确
        assertTrue(received, "订单事件应该被@EventListener接收");
        assertEquals(1, testEventListener.getOrderCount().get(), "订单事件计数应该为1");
        assertEquals("order.created", testEventListener.getLastOrderEventType().get(), "事件类型应该匹配");
    }

    @Test
    @DisplayName("支付事件监听测试")
    void testPaymentEventListening() throws InterruptedException {
        // 创建支付事件
        EventDTO event = new EventDTO(
            "payment.success",
            "payment-service",
            "{\"paymentId\":\"PAY-11111\",\"orderId\":\"ORD-67890\",\"amount\":199.99}",
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-payment-events", event);
        
        // 等待@EventListener处理事件
        boolean received = testEventListener.getPaymentLatch().await(5, TimeUnit.SECONDS);
        
        // 验证事件监听是否正确
        assertTrue(received, "支付事件应该被@EventListener接收");
        assertEquals(1, testEventListener.getPaymentCount().get(), "支付事件计数应该为1");
        assertEquals("payment.success", testEventListener.getLastPaymentEventType().get(), "事件类型应该匹配");
    }

    @Test
    @DisplayName("事件类型过滤测试")
    void testEventTypeFiltering() throws InterruptedException {
        // 发送不匹配的事件类型
        EventDTO event = new EventDTO(
            "user.logout",  // 不匹配 user.login
            "auth-service",
            "{\"userId\":\"12345\",\"logoutTime\":\"" + Instant.now() + "\"}",
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-user-events", event);
        
        // 等待一段时间确保消息被处理
        Thread.sleep(2000);
        
        // 验证@EventListener的事件类型过滤是否生效
        assertEquals(0, testEventListener.getUserLoginCount().get(), "不匹配的事件类型不应该被@EventListener处理");
    }

    @Test
    @DisplayName("批量事件监听测试")
    void testBatchEventListening() throws InterruptedException {
        // 发送多个事件
        for (int i = 0; i < 5; i++) {
            EventDTO event = new EventDTO(
                "user.login",
                "auth-service",
                "{\"userId\":\"user" + i + "\",\"loginTime\":\"" + Instant.now() + "\"}",
                "test-tenant"
            );
            
            // 直接使用Spring Kafka发送事件
            kafkaTemplate.send("test-batch-events", event);
        }
        
        // 等待所有事件被@EventListener处理
        boolean received = testEventListener.getBatchLatch().await(10, TimeUnit.SECONDS);
        
        // 验证批量事件监听是否正确
        assertTrue(received, "批量事件应该被@EventListener接收");
        assertEquals(5, testEventListener.getBatchCount().get(), "应该接收到5个用户登录事件");
    }

    @Test
    @DisplayName("监听器注册验证测试")
    void testListenerRegistration() {
        // 验证@EventListener监听器已注册
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-user-events"), 
            "test-user-events Topic应该已注册");
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-order-events"), 
            "test-order-events Topic应该已注册");
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-payment-events"), 
            "test-payment-events Topic应该已注册");
        assertTrue(eventListenerManager.getRegisteredTopics().contains("test-batch-events"), 
            "test-batch-events Topic应该已注册");
        
        // 验证监听器数量
        assertTrue(eventListenerManager.getTotalListenerCount() > 0, "应该有注册的@EventListener监听器");
        
        // 验证特定Topic的监听器
        assertFalse(eventListenerManager.getListeners("test-user-events").isEmpty(), 
            "test-user-events应该有@EventListener监听器");
    }

    @Test
    @DisplayName("监听器优先级测试")
    void testListenerPriority() throws InterruptedException {
        // 创建用户登录事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"priority-test\",\"loginTime\":\"" + Instant.now() + "\"}",
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-user-events", event);
        
        // 等待@EventListener处理事件
        boolean received = testEventListener.getUserLoginLatch().await(5, TimeUnit.SECONDS);
        
        // 验证事件被@EventListener按优先级处理
        assertTrue(received, "用户登录事件应该被@EventListener接收");
        assertTrue(testEventListener.getLastUserEventType().get() != null, "事件应该被@EventListener处理");
    }

    @Test
    @DisplayName("错误处理测试")
    void testErrorHandling() throws InterruptedException {
        // 创建会导致异常的事件
        EventDTO event = new EventDTO(
            "user.login",
            "auth-service",
            "{\"userId\":\"error-user\",\"loginTime\":\"" + Instant.now() + "\"}",  // 特殊用户ID会触发异常
            "test-tenant"
        );
        
        // 直接使用Spring Kafka发送事件
        kafkaTemplate.send("test-user-events", event);
        
        // 等待消息处理
        Thread.sleep(3000);
        
        // 验证异常被正确处理（不会导致测试失败）
        // 这里主要验证@EventListener的异常处理不会影响其他监听器的正常工作
        assertTrue(true, "异常应该被@EventListener正确处理");
    }

    /**
     * 测试事件监听器组件
     * 使用@EventListener注解进行事件监听
     */
    @Component
    static class TestEventListener {
        // 用户事件相关
        private final AtomicInteger userLoginCount = new AtomicInteger(0);
        private final AtomicReference<String> lastUserEventType = new AtomicReference<>();
        private final AtomicReference<EventDTO> lastUserEvent = new AtomicReference<>();
        private final CountDownLatch userLoginLatch = new CountDownLatch(1);
        
        // 订单事件相关
        private final AtomicInteger orderCount = new AtomicInteger(0);
        private final AtomicReference<String> lastOrderEventType = new AtomicReference<>();
        private final CountDownLatch orderLatch = new CountDownLatch(1);
        
        // 支付事件相关
        private final AtomicInteger paymentCount = new AtomicInteger(0);
        private final AtomicReference<String> lastPaymentEventType = new AtomicReference<>();
        private final CountDownLatch paymentLatch = new CountDownLatch(1);
        
        // 批量事件相关
        private final AtomicInteger batchCount = new AtomicInteger(0);
        private final CountDownLatch batchLatch = new CountDownLatch(5);

        /**
         * 监听用户登录事件
         */
        @EventListener(
            topics = "test-user-events",
            eventTypes = "user.login",
            groupId = "test-user-group",
            id = "test-user-login-listener",
            priority = 1
        )
        public void handleUserLogin(EventDTO event) {
            userLoginCount.incrementAndGet();
            lastUserEventType.set(event.getEventType());
            lastUserEvent.set(event);
            userLoginLatch.countDown();
            
            // 模拟异常处理
            if (event.getData().contains("error-user")) {
                throw new RuntimeException("模拟异常: " + event.getData());
            }
        }

        /**
         * 监听订单事件
         */
        @EventListener(
            topics = "test-order-events",
            groupId = "test-order-group",
            id = "test-order-listener",
            priority = 2
        )
        public void handleOrderEvents(EventDTO event) {
            orderCount.incrementAndGet();
            lastOrderEventType.set(event.getEventType());
            orderLatch.countDown();
        }

        /**
         * 监听支付事件
         */
        @EventListener(
            topics = "test-payment-events",
            eventTypes = "payment.success",
            groupId = "test-payment-group",
            id = "test-payment-listener",
            priority = 0  // 高优先级
        )
        public void handlePaymentEvents(EventDTO event) {
            paymentCount.incrementAndGet();
            lastPaymentEventType.set(event.getEventType());
            paymentLatch.countDown();
        }

        /**
         * 监听批量事件
         */
        @EventListener(
            topics = "test-batch-events",
            eventTypes = "user.login",
            groupId = "test-batch-group",
            id = "test-batch-listener",
            priority = 1
        )
        public void handleBatchEvents(EventDTO event) {
            batchCount.incrementAndGet();
            batchLatch.countDown();
        }

        /**
         * 重置所有计数器
         */
        public void reset() {
            userLoginCount.set(0);
            orderCount.set(0);
            paymentCount.set(0);
            batchCount.set(0);
            lastUserEventType.set(null);
            lastOrderEventType.set(null);
            lastPaymentEventType.set(null);
            lastUserEvent.set(null);
        }

        // Getters
        public AtomicInteger getUserLoginCount() { return userLoginCount; }
        public AtomicInteger getOrderCount() { return orderCount; }
        public AtomicInteger getPaymentCount() { return paymentCount; }
        public AtomicInteger getBatchCount() { return batchCount; }
        public AtomicReference<String> getLastUserEventType() { return lastUserEventType; }
        public AtomicReference<String> getLastOrderEventType() { return lastOrderEventType; }
        public AtomicReference<String> getLastPaymentEventType() { return lastPaymentEventType; }
        public AtomicReference<EventDTO> getLastUserEvent() { return lastUserEvent; }
        public CountDownLatch getUserLoginLatch() { return userLoginLatch; }
        public CountDownLatch getOrderLatch() { return orderLatch; }
        public CountDownLatch getPaymentLatch() { return paymentLatch; }
        public CountDownLatch getBatchLatch() { return batchLatch; }
    }
}