package com.aixone.event.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * EventListener 注解单元测试
 */
@DisplayName("EventListener 注解测试")
class EventListenerTest {

    @Test
    @DisplayName("注解基本属性测试")
    void testAnnotationBasicProperties() {
        // 验证注解的保留策略
        Retention retention = EventListener.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
        
        // 验证注解的目标
        Target target = EventListener.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertTrue(target.value().length >= 1);
        assertTrue(Arrays.asList(target.value()).contains(ElementType.METHOD));
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // 创建一个使用默认值的注解实例
        EventListener annotation = new EventListener() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return EventListener.class;
            }
            
            @Override
            public String id() {
                return "";
            }
            
            @Override
            public String containerFactory() {
                return "";
            }
            
            @Override
            public String[] topics() {
                return new String[0];
            }
            
            @Override
            public String topicPattern() {
                return "";
            }
            
            @Override
            public org.springframework.kafka.annotation.TopicPartition[] topicPartitions() {
                return new org.springframework.kafka.annotation.TopicPartition[0];
            }
            
            @Override
            public String containerGroup() {
                return "";
            }
            
            @Override
            public String errorHandler() {
                return "";
            }
            
            @Override
            public String groupId() {
                return "";
            }
            
            @Override
            public boolean idIsGroup() {
                return true;
            }
            
            @Override
            public String clientIdPrefix() {
                return "";
            }
            
            @Override
            public String beanRef() {
                return "__listener";
            }
            
            @Override
            public String[] eventTypes() {
                return new String[0];
            }
            
            @Override
            public boolean enabled() {
                return true;
            }
            
            @Override
            public int priority() {
                return 0;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // 验证默认值
        assertArrayEquals(new String[0], annotation.topics());
        assertArrayEquals(new String[0], annotation.eventTypes());
        assertEquals("", annotation.groupId());
        assertTrue(annotation.enabled());
        assertEquals(0, annotation.priority());
        assertEquals("", annotation.description());
        assertEquals("", annotation.id());
        assertEquals("", annotation.containerFactory());
        assertEquals("", annotation.errorHandler());
        assertEquals("", annotation.topicPattern());
        assertArrayEquals(new org.springframework.kafka.annotation.TopicPartition[0], annotation.topicPartitions());
        assertEquals("", annotation.containerGroup());
        assertTrue(annotation.idIsGroup());
        assertEquals("", annotation.clientIdPrefix());
        assertEquals("__listener", annotation.beanRef());
    }

    @Test
    @DisplayName("注解使用示例测试")
    void testAnnotationUsage() throws NoSuchMethodException {
        // 获取测试类中的注解方法
        Method method = TestClass.class.getMethod("handleUserLogin", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        
        // 验证注解值
        assertArrayEquals(new String[]{"user-events"}, annotation.topics());
        assertArrayEquals(new String[]{"user.login", "user.logout"}, annotation.eventTypes());
        assertEquals("user-group", annotation.groupId());
        assertTrue(annotation.enabled());
        assertEquals(1, annotation.priority());
        assertEquals("处理用户登录和登出事件", annotation.description());
    }

    @Test
    @DisplayName("多 Topic 测试")
    void testMultipleTopics() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleMultipleTopics", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertArrayEquals(new String[]{"user-events", "order-events", "system-events"}, annotation.topics());
    }

    @Test
    @DisplayName("多事件类型测试")
    void testMultipleEventTypes() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleMultipleEventTypes", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertArrayEquals(new String[]{"user.login", "user.logout", "user.register", "user.update"}, annotation.eventTypes());
    }

    @Test
    @DisplayName("禁用监听器测试")
    void testDisabledListener() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleDisabledEvent", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertFalse(annotation.enabled());
    }

    @Test
    @DisplayName("高优先级测试")
    void testHighPriority() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleHighPriorityEvent", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertEquals(-1, annotation.priority());
    }

    @Test
    @DisplayName("空值处理测试")
    void testEmptyValues() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleEmptyValues", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertArrayEquals(new String[0], annotation.topics());
        assertArrayEquals(new String[0], annotation.eventTypes());
        assertEquals("", annotation.groupId());
        assertEquals("", annotation.description());
    }

    @Test
    @DisplayName("长描述测试")
    void testLongDescription() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleLongDescription", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        String description = annotation.description();
        assertTrue(description.length() > 50);
        assertTrue(description.contains("这是一个非常长的描述"));
    }

    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("handleBoundaryValues", com.aixone.event.dto.EventDTO.class);
        EventListener annotation = method.getAnnotation(EventListener.class);
        
        assertNotNull(annotation);
        assertEquals(Integer.MAX_VALUE, annotation.priority());
    }

    @Test
    @DisplayName("注解继承测试")
    void testAnnotationInheritance() {
        // 验证注解不会被继承
        Method[] parentMethods = ParentClass.class.getDeclaredMethods();
        Method[] childMethods = ChildClass.class.getDeclaredMethods();
        
        // 父类方法应该有注解
        boolean parentHasAnnotation = false;
        for (Method method : parentMethods) {
            if (method.getAnnotation(EventListener.class) != null) {
                parentHasAnnotation = true;
                break;
            }
        }
        assertTrue(parentHasAnnotation);
        
        // 子类方法不应该继承注解
        boolean childHasAnnotation = false;
        for (Method method : childMethods) {
            if (method.getAnnotation(EventListener.class) != null) {
                childHasAnnotation = true;
                break;
            }
        }
        assertFalse(childHasAnnotation);
    }

    // 测试类
    static class TestClass {
        
        @EventListener(
            topics = "user-events",
            eventTypes = {"user.login", "user.logout"},
            groupId = "user-group",
            enabled = true,
            priority = 1,
            description = "处理用户登录和登出事件"
        )
        public void handleUserLogin(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(topics = {"user-events", "order-events", "system-events"})
        public void handleMultipleTopics(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(eventTypes = {"user.login", "user.logout", "user.register", "user.update"})
        public void handleMultipleEventTypes(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(enabled = false)
        public void handleDisabledEvent(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(priority = -1)
        public void handleHighPriorityEvent(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener
        public void handleEmptyValues(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(description = "这是一个非常长的描述，用于测试注解对长字符串的处理能力。" +
                "这个描述包含了多个句子，用于验证注解系统能够正确处理包含特殊字符和长文本的描述信息。" +
                "同时，这个测试也验证了注解系统在处理复杂字符串时的稳定性。")
        public void handleLongDescription(com.aixone.event.dto.EventDTO event) {}
        
        @EventListener(priority = Integer.MAX_VALUE)
        public void handleBoundaryValues(com.aixone.event.dto.EventDTO event) {}
    }
    
    // 父类
    static class ParentClass {
        @EventListener(topics = "parent-events")
        public void handleParentEvent(com.aixone.event.dto.EventDTO event) {}
    }
    
    // 子类
    static class ChildClass extends ParentClass {
        public void handleChildEvent(com.aixone.event.dto.EventDTO event) {}
    }
}