package com.aixone.event.listener;

import com.aixone.event.annotation.EventListener;
import com.aixone.event.annotation.EventListeners;
import com.aixone.event.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听器管理器
 * 基于Spring Kafka的@KafkaListener，提供统一的事件监听能力
 * 支持多种MQ中间件的封装和扩展
 */
@Component
public class EventListenerManager implements ApplicationContextAware {
    
    private static final Logger logger = LoggerFactory.getLogger(EventListenerManager.class);
    
    private ApplicationContext applicationContext;
    
    /**
     * 监听器注册表
     * Key: Topic名称, Value: 监听器信息列表
     */
    private final Map<String, List<ListenerInfo>> listenerRegistry = new ConcurrentHashMap<>();
    
    /**
     * 监听器信息
     */
    public static class ListenerInfo {
        private final Object bean;
        private final Method method;
        private final EventListener annotation;
        private final String id;
        private final String[] topics;
        private final String topicPattern;
        private final TopicPartition[] topicPartitions;
        private final String containerGroup;
        private final String errorHandler;
        private final String groupId;
        private final boolean idIsGroup;
        private final String clientIdPrefix;
        private final String beanRef;
        private final String[] eventTypes;
        private final boolean enabled;
        private final int priority;
        private final String description;
        
        public ListenerInfo(Object bean, Method method, EventListener annotation) {
            this.bean = bean;
            this.method = method;
            this.annotation = annotation;
            this.id = annotation.id();
            this.topics = annotation.topics();
            this.topicPattern = annotation.topicPattern();
            this.topicPartitions = annotation.topicPartitions();
            this.containerGroup = annotation.containerGroup();
            this.errorHandler = annotation.errorHandler();
            this.groupId = annotation.groupId();
            this.idIsGroup = annotation.idIsGroup();
            this.clientIdPrefix = annotation.clientIdPrefix();
            this.beanRef = annotation.beanRef();
            this.eventTypes = annotation.eventTypes();
            this.enabled = annotation.enabled();
            this.priority = annotation.priority();
            this.description = annotation.description();
        }
        
        /**
         * 检查事件类型是否匹配
         */
        public boolean isEventTypeMatch(String eventType) {
            if (eventTypes.length == 0) {
                return true; // 没有指定事件类型，匹配所有
            }
            return Arrays.stream(eventTypes).anyMatch(type -> type.equals(eventType));
        }
        
        /**
         * 获取有效的Topic列表
         */
        public String[] getEffectiveTopics() {
            if (topics.length > 0) {
                return topics;
            }
            if (topicPartitions.length > 0) {
                return Arrays.stream(topicPartitions)
                    .map(TopicPartition::topic)
                    .distinct()
                    .toArray(String[]::new);
            }
            return new String[0];
        }
        
        // Getters
        public Object getBean() { return bean; }
        public Method getMethod() { return method; }
        public EventListener getAnnotation() { return annotation; }
        public String getId() { return id; }
        public String[] getTopics() { return topics; }
        public String getTopicPattern() { return topicPattern; }
        public TopicPartition[] getTopicPartitions() { return topicPartitions; }
        public String getContainerGroup() { return containerGroup; }
        public String getErrorHandler() { return errorHandler; }
        public String getGroupId() { return groupId; }
        public boolean isIdIsGroup() { return idIsGroup; }
        public String getClientIdPrefix() { return clientIdPrefix; }
        public String getBeanRef() { return beanRef; }
        public String[] getEventTypes() { return eventTypes; }
        public boolean isEnabled() { return enabled; }
        public int getPriority() { return priority; }
        public String getDescription() { return description; }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        scanAndRegisterListeners();
    }
    
    /**
     * 扫描并注册所有事件监听器
     */
    private void scanAndRegisterListeners() {
        logger.info("开始扫描事件监听器...");
        
        // 获取所有Bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();
            
            // 扫描类中的所有方法
            Method[] methods = beanClass.getDeclaredMethods();
            for (Method method : methods) {
                // 处理单个@EventListener注解
                EventListener eventListener = AnnotationUtils.findAnnotation(method, EventListener.class);
                if (eventListener != null) {
                    registerListener(bean, method, eventListener);
                }
                
                // 处理@EventListeners重复注解
                EventListeners eventListeners = AnnotationUtils.findAnnotation(method, EventListeners.class);
                if (eventListeners != null) {
                    for (EventListener listener : eventListeners.value()) {
                        registerListener(bean, method, listener);
                    }
                }
            }
        }
        
        logger.info("事件监听器扫描完成，共注册 {} 个监听器", getTotalListenerCount());
    }
    
    /**
     * 注册单个监听器
     */
    private void registerListener(Object bean, Method method, EventListener annotation) {
        try {
            ListenerInfo listenerInfo = new ListenerInfo(bean, method, annotation);
            
            // 获取有效的Topic列表
            String[] effectiveTopics = listenerInfo.getEffectiveTopics();
            
            if (effectiveTopics.length == 0 && listenerInfo.getTopicPattern().isEmpty()) {
                logger.warn("监听器没有指定Topic或Topic模式 - Bean: {}, Method: {}", 
                    bean.getClass().getSimpleName(), method.getName());
                return;
            }
            
            // 注册到对应的Topic
            for (String topic : effectiveTopics) {
                listenerRegistry.computeIfAbsent(topic, k -> new ArrayList<>())
                    .add(listenerInfo);
                
                logger.info("注册事件监听器 - Topic: {}, Method: {}.{}, EventTypes: {}, ID: {}", 
                    topic, bean.getClass().getSimpleName(), method.getName(), 
                    Arrays.toString(annotation.eventTypes()), annotation.id());
            }
            
        } catch (Exception e) {
            logger.error("注册事件监听器失败 - Bean: {}, Method: {}", 
                bean.getClass().getSimpleName(), method.getName(), e);
        }
    }
    
    /**
     * 获取指定Topic的监听器列表
     */
    public List<ListenerInfo> getListeners(String topic) {
        return listenerRegistry.getOrDefault(topic, Collections.emptyList());
    }
    
    /**
     * 获取所有注册的Topic
     */
    public Set<String> getRegisteredTopics() {
        return listenerRegistry.keySet();
    }
    
    /**
     * 获取监听器总数
     */
    public int getTotalListenerCount() {
        return listenerRegistry.values().stream()
            .mapToInt(List::size)
            .sum();
    }
    
    /**
     * 获取所有监听器（按Topic分组）
     */
    public Map<String, List<ListenerInfo>> getAllListeners() {
        return new HashMap<>(listenerRegistry);
    }
    
    /**
     * 根据ID获取监听器
     */
    public ListenerInfo getListenerById(String id) {
        return listenerRegistry.values().stream()
            .flatMap(List::stream)
            .filter(listener -> id.equals(listener.getId()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 根据容器组获取监听器
     */
    public Map<String, List<ListenerInfo>> getListenersByContainerGroup(String containerGroup) {
        Map<String, List<ListenerInfo>> result = new HashMap<>();
        listenerRegistry.forEach((topic, listeners) -> {
            List<ListenerInfo> filteredListeners = listeners.stream()
                .filter(listener -> containerGroup.equals(listener.getContainerGroup()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            if (!filteredListeners.isEmpty()) {
                result.put(topic, filteredListeners);
            }
        });
        return result;
    }
}