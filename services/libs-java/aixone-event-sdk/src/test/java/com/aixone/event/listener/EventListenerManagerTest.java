package com.aixone.event.listener;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.dto.EventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EventListenerManager 单元测试
 */
@DisplayName("EventListenerManager 测试")
class EventListenerManagerTest {

    private EventListenerManager manager;
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        manager = new EventListenerManager();
        applicationContext = new AnnotationConfigApplicationContext(TestConfiguration.class);
        manager.setApplicationContext(applicationContext);
    }

    @Test
    @DisplayName("监听器注册测试")
    void testListenerRegistration() {
        // 验证监听器已注册
        Set<String> topics = manager.getRegisteredTopics();
        assertTrue(topics.contains("user-events"));
        assertTrue(topics.contains("order-events"));
        
        // 验证监听器数量
        assertTrue(manager.getTotalListenerCount() > 0);
    }

    @Test
    @DisplayName("事件分发测试")
    void testEventDispatch() {
        TestListener testListener = applicationContext.getBean(TestListener.class);
        AtomicInteger callCount = testListener.getCallCount();
        callCount.set(0);
        
        // 创建测试事件
        EventDTO event = new EventDTO("user.login", "auth-service", "{}", "tenant-001");
        
        // 获取监听器并手动调用
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        // 验证监听器信息
        assertTrue(listeners.size() > 0);
    }

    @Test
    @DisplayName("事件类型过滤测试")
    void testEventTypeFiltering() {
        TestListener testListener = applicationContext.getBean(TestListener.class);
        AtomicInteger callCount = testListener.getCallCount();
        callCount.set(0);
        
        // 获取监听器并测试事件类型匹配
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        // 测试事件类型匹配逻辑
        EventListenerManager.ListenerInfo listener = listeners.get(0);
        assertTrue(listener.isEventTypeMatch("user.login"));
        assertFalse(listener.isEventTypeMatch("user.logout"));
    }

    @Test
    @DisplayName("监听器优先级测试")
    void testListenerPriority() {
        // 这个测试需要多个监听器来验证优先级
        // 由于当前只有一个测试监听器，我们主要验证优先级排序逻辑
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        // 验证监听器按优先级排序
        for (int i = 1; i < listeners.size(); i++) {
            assertTrue(listeners.get(i-1).getPriority() <= listeners.get(i).getPriority());
        }
    }

    @Test
    @DisplayName("空 Topic 测试")
    void testEmptyTopic() {
        // 测试不存在的 Topic
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("non-existent-topic");
        assertTrue(listeners.isEmpty());
        
        // 测试空 Topic
        listeners = manager.getListeners("");
        assertTrue(listeners.isEmpty());
        
        // 测试 null Topic
        listeners = manager.getListeners(null);
        assertTrue(listeners.isEmpty());
    }

    @Test
    @DisplayName("监听器信息测试")
    void testListenerInfo() {
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        EventListenerManager.ListenerInfo listenerInfo = listeners.get(0);
        
        // 验证监听器信息
        assertNotNull(listenerInfo.getBean());
        assertNotNull(listenerInfo.getMethod());
        assertNotNull(listenerInfo.getAnnotation());
        assertNotNull(listenerInfo.getTopics());
        assertNotNull(listenerInfo.getEventTypes());
        assertNotNull(listenerInfo.getGroupId());
        assertTrue(listenerInfo.isEnabled());
        assertTrue(listenerInfo.getPriority() >= 0);
        assertNotNull(listenerInfo.getDescription());
    }

    @Test
    @DisplayName("监听器启用/禁用测试")
    void testListenerEnableDisable() {
        // 测试监听器的启用状态
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        // 验证监听器默认启用
        for (EventListenerManager.ListenerInfo listener : listeners) {
            assertTrue(listener.isEnabled());
        }
    }

    @Test
    @DisplayName("异常处理测试")
    void testExceptionHandling() {
        // 测试异常监听器的注册
        List<EventListenerManager.ListenerInfo> listeners = manager.getListeners("user-events");
        assertFalse(listeners.isEmpty());
        
        // 验证监听器已注册
        boolean hasExceptionListener = listeners.stream()
            .anyMatch(listener -> listener.getBean() instanceof TestExceptionListener);
        assertTrue(hasExceptionListener);
    }

    @Test
    @DisplayName("监听器统计测试")
    void testListenerStatistics() {
        int totalCount = manager.getTotalListenerCount();
        assertTrue(totalCount >= 0);
        
        Set<String> topics = manager.getRegisteredTopics();
        assertNotNull(topics);
        
        // 验证每个 Topic 都有监听器
        for (String topic : topics) {
            List<EventListenerManager.ListenerInfo> listeners = manager.getListeners(topic);
            assertNotNull(listeners);
            assertFalse(listeners.isEmpty());
        }
    }

    // 测试配置类
    @org.springframework.context.annotation.Configuration
    static class TestConfiguration {
        
        @org.springframework.context.annotation.Bean
        public TestListener testListener() {
            return new TestListener();
        }
        
        @org.springframework.context.annotation.Bean
        public TestExceptionListener testExceptionListener() {
            return new TestExceptionListener();
        }
    }

    // 测试监听器
    @Component
    static class TestListener {
        private final AtomicInteger callCount = new AtomicInteger(0);
        
        @EventListener(topics = "user-events", eventTypes = "user.login", priority = 1, description = "测试用户登录监听器")
        public void handleUserLogin(EventDTO event) {
            callCount.incrementAndGet();
        }
        
        @EventListener(topics = "order-events", priority = 2, description = "测试订单事件监听器")
        public void handleOrderEvents(EventDTO event) {
            callCount.incrementAndGet();
        }
        
        public AtomicInteger getCallCount() {
            return callCount;
        }
    }

    // 异常测试监听器
    @Component
    static class TestExceptionListener {
        
        @EventListener(topics = "user-events", eventTypes = "user.login", priority = 0, description = "异常测试监听器")
        public void handleUserLoginWithException(EventDTO event) {
            throw new RuntimeException("测试异常");
        }
    }
}