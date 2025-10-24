package com.aixone.event.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EventProperties 单元测试
 */
@DisplayName("EventProperties 测试")
class EventPropertiesTest {

    private EventProperties properties;

    @BeforeEach
    void setUp() {
        properties = new EventProperties();
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        // 根属性默认值
        assertTrue(properties.isEnabled());
        
        // EventCenter 默认值
        EventProperties.EventCenter eventCenter = properties.getEventCenter();
        assertNotNull(eventCenter);
        assertEquals("http://localhost:8080", eventCenter.getBaseUrl());
        assertEquals("default", eventCenter.getTenantId());
        assertEquals(5000, eventCenter.getConnectTimeout());
        assertEquals(10000, eventCenter.getReadTimeout());
        
        // Kafka 默认值
        EventProperties.Kafka kafka = properties.getKafka();
        assertNotNull(kafka);
        assertEquals("localhost:9092", kafka.getBootstrapServers());
        assertEquals("aixone-event-group", kafka.getGroupId());
        assertEquals("earliest", kafka.getAutoOffsetReset());
        assertTrue(kafka.isEnableAutoCommit());
        assertEquals(1000, kafka.getAutoCommitIntervalMs());
        assertEquals(30000, kafka.getSessionTimeoutMs());
        assertEquals(500, kafka.getMaxPollRecords());
        assertEquals(1, kafka.getConcurrency());
        assertEquals(1000, kafka.getPollTimeout());
        assertNull(kafka.getSecurity());
        
        // Listener 默认值
        EventProperties.Listener listener = properties.getListener();
        assertNotNull(listener);
        assertTrue(listener.isAutoStart());
        assertEquals(10, listener.getThreadPoolSize());
        assertEquals(1000, listener.getQueueCapacity());
        assertTrue(listener.isEnableMetrics());
    }

    @Test
    @DisplayName("根属性测试")
    void testRootProperties() {
        // 测试 enabled
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());
        
        properties.setEnabled(true);
        assertTrue(properties.isEnabled());
    }

    @Test
    @DisplayName("EventCenter 配置测试")
    void testEventCenterConfiguration() {
        EventProperties.EventCenter eventCenter = new EventProperties.EventCenter();
        
        // 测试 baseUrl
        String baseUrl = "http://custom-host:9090";
        eventCenter.setBaseUrl(baseUrl);
        assertEquals(baseUrl, eventCenter.getBaseUrl());
        
        // 测试 tenantId
        String tenantId = "custom-tenant";
        eventCenter.setTenantId(tenantId);
        assertEquals(tenantId, eventCenter.getTenantId());
        
        // 测试 connectTimeout
        int connectTimeout = 10000;
        eventCenter.setConnectTimeout(connectTimeout);
        assertEquals(connectTimeout, eventCenter.getConnectTimeout());
        
        // 测试 readTimeout
        int readTimeout = 20000;
        eventCenter.setReadTimeout(readTimeout);
        assertEquals(readTimeout, eventCenter.getReadTimeout());
        
        // 设置到主配置
        properties.setEventCenter(eventCenter);
        assertEquals(eventCenter, properties.getEventCenter());
    }

    @Test
    @DisplayName("Kafka 配置测试")
    void testKafkaConfiguration() {
        EventProperties.Kafka kafka = new EventProperties.Kafka();
        
        // 测试 bootstrapServers
        String bootstrapServers = "kafka1:9092,kafka2:9092";
        kafka.setBootstrapServers(bootstrapServers);
        assertEquals(bootstrapServers, kafka.getBootstrapServers());
        
        // 测试 groupId
        String groupId = "custom-group";
        kafka.setGroupId(groupId);
        assertEquals(groupId, kafka.getGroupId());
        
        // 测试 autoOffsetReset
        String autoOffsetReset = "latest";
        kafka.setAutoOffsetReset(autoOffsetReset);
        assertEquals(autoOffsetReset, kafka.getAutoOffsetReset());
        
        // 测试 enableAutoCommit
        kafka.setEnableAutoCommit(false);
        assertFalse(kafka.isEnableAutoCommit());
        
        // 测试 autoCommitIntervalMs
        int autoCommitIntervalMs = 2000;
        kafka.setAutoCommitIntervalMs(autoCommitIntervalMs);
        assertEquals(autoCommitIntervalMs, kafka.getAutoCommitIntervalMs());
        
        // 测试 sessionTimeoutMs
        int sessionTimeoutMs = 60000;
        kafka.setSessionTimeoutMs(sessionTimeoutMs);
        assertEquals(sessionTimeoutMs, kafka.getSessionTimeoutMs());
        
        // 测试 maxPollRecords
        int maxPollRecords = 1000;
        kafka.setMaxPollRecords(maxPollRecords);
        assertEquals(maxPollRecords, kafka.getMaxPollRecords());
        
        // 测试 concurrency
        int concurrency = 3;
        kafka.setConcurrency(concurrency);
        assertEquals(concurrency, kafka.getConcurrency());
        
        // 测试 pollTimeout
        int pollTimeout = 2000;
        kafka.setPollTimeout(pollTimeout);
        assertEquals(pollTimeout, kafka.getPollTimeout());
        
        // 设置到主配置
        properties.setKafka(kafka);
        assertEquals(kafka, properties.getKafka());
    }

    @Test
    @DisplayName("Kafka 安全配置测试")
    void testKafkaSecurityConfiguration() {
        EventProperties.Kafka kafka = new EventProperties.Kafka();
        EventProperties.KafkaSecurity security = new EventProperties.KafkaSecurity();
        
        // 测试 securityProtocol
        String securityProtocol = "SASL_SSL";
        security.setSecurityProtocol(securityProtocol);
        assertEquals(securityProtocol, security.getSecurityProtocol());
        
        // 测试 saslMechanism
        String saslMechanism = "PLAIN";
        security.setSaslMechanism(saslMechanism);
        assertEquals(saslMechanism, security.getSaslMechanism());
        
        // 测试 saslJaasConfig
        String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";";
        security.setSaslJaasConfig(saslJaasConfig);
        assertEquals(saslJaasConfig, security.getSaslJaasConfig());
        
        // 设置到 Kafka 配置
        kafka.setSecurity(security);
        assertEquals(security, kafka.getSecurity());
        
        // 设置到主配置
        properties.setKafka(kafka);
        assertEquals(kafka, properties.getKafka());
    }

    @Test
    @DisplayName("Listener 配置测试")
    void testListenerConfiguration() {
        EventProperties.Listener listener = new EventProperties.Listener();
        
        // 测试 autoStart
        listener.setAutoStart(false);
        assertFalse(listener.isAutoStart());
        
        // 测试 threadPoolSize
        int threadPoolSize = 20;
        listener.setThreadPoolSize(threadPoolSize);
        assertEquals(threadPoolSize, listener.getThreadPoolSize());
        
        // 测试 queueCapacity
        int queueCapacity = 2000;
        listener.setQueueCapacity(queueCapacity);
        assertEquals(queueCapacity, listener.getQueueCapacity());
        
        // 测试 enableMetrics
        listener.setEnableMetrics(false);
        assertFalse(listener.isEnableMetrics());
        
        // 设置到主配置
        properties.setListener(listener);
        assertEquals(listener, properties.getListener());
    }

    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() {
        EventProperties.EventCenter eventCenter = properties.getEventCenter();
        EventProperties.Kafka kafka = properties.getKafka();
        EventProperties.Listener listener = properties.getListener();
        
        // 测试零值
        eventCenter.setConnectTimeout(0);
        assertEquals(0, eventCenter.getConnectTimeout());
        
        eventCenter.setReadTimeout(0);
        assertEquals(0, eventCenter.getReadTimeout());
        
        kafka.setAutoCommitIntervalMs(0);
        assertEquals(0, kafka.getAutoCommitIntervalMs());
        
        kafka.setSessionTimeoutMs(0);
        assertEquals(0, kafka.getSessionTimeoutMs());
        
        kafka.setMaxPollRecords(0);
        assertEquals(0, kafka.getMaxPollRecords());
        
        kafka.setConcurrency(0);
        assertEquals(0, kafka.getConcurrency());
        
        kafka.setPollTimeout(0);
        assertEquals(0, kafka.getPollTimeout());
        
        listener.setThreadPoolSize(0);
        assertEquals(0, listener.getThreadPoolSize());
        
        listener.setQueueCapacity(0);
        assertEquals(0, listener.getQueueCapacity());
        
        // 测试负值
        eventCenter.setConnectTimeout(-1);
        assertEquals(-1, eventCenter.getConnectTimeout());
        
        kafka.setConcurrency(-1);
        assertEquals(-1, kafka.getConcurrency());
        
        listener.setThreadPoolSize(-1);
        assertEquals(-1, listener.getThreadPoolSize());
    }

    @Test
    @DisplayName("空值测试")
    void testNullValues() {
        EventProperties.EventCenter eventCenter = properties.getEventCenter();
        EventProperties.Kafka kafka = properties.getKafka();
        
        // 测试 null 值设置
        eventCenter.setBaseUrl(null);
        assertNull(eventCenter.getBaseUrl());
        
        eventCenter.setTenantId(null);
        assertNull(eventCenter.getTenantId());
        
        kafka.setBootstrapServers(null);
        assertNull(kafka.getBootstrapServers());
        
        kafka.setGroupId(null);
        assertNull(kafka.getGroupId());
        
        kafka.setAutoOffsetReset(null);
        assertNull(kafka.getAutoOffsetReset());
        
        kafka.setSecurity(null);
        assertNull(kafka.getSecurity());
    }

    @Test
    @DisplayName("长字符串测试")
    void testLongStrings() {
        EventProperties.EventCenter eventCenter = properties.getEventCenter();
        EventProperties.Kafka kafka = properties.getKafka();
        EventProperties.KafkaSecurity security = new EventProperties.KafkaSecurity();
        
        // 测试长 URL
        String longUrl = "http://" + "a".repeat(1000) + ".com";
        eventCenter.setBaseUrl(longUrl);
        assertEquals(longUrl, eventCenter.getBaseUrl());
        
        // 测试长 tenantId
        String longTenantId = "tenant-" + "b".repeat(1000);
        eventCenter.setTenantId(longTenantId);
        assertEquals(longTenantId, eventCenter.getTenantId());
        
        // 测试长 groupId
        String longGroupId = "group-" + "c".repeat(1000);
        kafka.setGroupId(longGroupId);
        assertEquals(longGroupId, kafka.getGroupId());
        
        // 测试长 saslJaasConfig
        String longSaslConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + "d".repeat(1000) + "\" password=\"" + "e".repeat(1000) + "\";";
        security.setSaslJaasConfig(longSaslConfig);
        assertEquals(longSaslConfig, security.getSaslJaasConfig());
    }
}