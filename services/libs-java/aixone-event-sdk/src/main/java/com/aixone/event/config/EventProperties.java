package com.aixone.event.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 事件配置属性
 */
@ConfigurationProperties(prefix = "aixone.event")
public class EventProperties {
    
    /**
     * 是否启用事件功能
     */
    private boolean enabled = true;
    
    /**
     * 事件中心配置
     */
    private EventCenter eventCenter = new EventCenter();
    
    /**
     * Kafka配置
     */
    private Kafka kafka = new Kafka();
    
    /**
     * 监听器配置
     */
    private Listener listener = new Listener();
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public EventCenter getEventCenter() { return eventCenter; }
    public void setEventCenter(EventCenter eventCenter) { this.eventCenter = eventCenter; }
    
    public Kafka getKafka() { return kafka; }
    public void setKafka(Kafka kafka) { this.kafka = kafka; }
    
    public Listener getListener() { return listener; }
    public void setListener(Listener listener) { this.listener = listener; }
    
    /**
     * 事件中心配置
     */
    public static class EventCenter {
        private String baseUrl = "http://localhost:8080";
        private String tenantId = "default";
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
        
        // Getters and Setters
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public int getConnectTimeout() { return connectTimeout; }
        public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }
        
        public int getReadTimeout() { return readTimeout; }
        public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
    }
    
    /**
     * Kafka配置
     */
    public static class Kafka {
        private String bootstrapServers = "localhost:9092";
        private String groupId = "aixone-event-group";
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = true;
        private int autoCommitIntervalMs = 1000;
        private int sessionTimeoutMs = 30000;
        private int maxPollRecords = 500;
        private int concurrency = 1;
        private int pollTimeout = 1000;
        private KafkaSecurity security;
        
        // Getters and Setters
        public String getBootstrapServers() { return bootstrapServers; }
        public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }
        
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        
        public String getAutoOffsetReset() { return autoOffsetReset; }
        public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }
        
        public boolean isEnableAutoCommit() { return enableAutoCommit; }
        public void setEnableAutoCommit(boolean enableAutoCommit) { this.enableAutoCommit = enableAutoCommit; }
        
        public int getAutoCommitIntervalMs() { return autoCommitIntervalMs; }
        public void setAutoCommitIntervalMs(int autoCommitIntervalMs) { this.autoCommitIntervalMs = autoCommitIntervalMs; }
        
        public int getSessionTimeoutMs() { return sessionTimeoutMs; }
        public void setSessionTimeoutMs(int sessionTimeoutMs) { this.sessionTimeoutMs = sessionTimeoutMs; }
        
        public int getMaxPollRecords() { return maxPollRecords; }
        public void setMaxPollRecords(int maxPollRecords) { this.maxPollRecords = maxPollRecords; }
        
        public int getConcurrency() { return concurrency; }
        public void setConcurrency(int concurrency) { this.concurrency = concurrency; }
        
        public int getPollTimeout() { return pollTimeout; }
        public void setPollTimeout(int pollTimeout) { this.pollTimeout = pollTimeout; }
        
        public KafkaSecurity getSecurity() { return security; }
        public void setSecurity(KafkaSecurity security) { this.security = security; }
    }
    
    /**
     * Kafka安全配置
     */
    public static class KafkaSecurity {
        private String securityProtocol = "PLAINTEXT";
        private String saslMechanism;
        private String saslJaasConfig;
        
        // Getters and Setters
        public String getSecurityProtocol() { return securityProtocol; }
        public void setSecurityProtocol(String securityProtocol) { this.securityProtocol = securityProtocol; }
        
        public String getSaslMechanism() { return saslMechanism; }
        public void setSaslMechanism(String saslMechanism) { this.saslMechanism = saslMechanism; }
        
        public String getSaslJaasConfig() { return saslJaasConfig; }
        public void setSaslJaasConfig(String saslJaasConfig) { this.saslJaasConfig = saslJaasConfig; }
    }
    
    /**
     * 监听器配置
     */
    public static class Listener {
        private boolean autoStart = true;
        private int threadPoolSize = 10;
        private int queueCapacity = 1000;
        private boolean enableMetrics = true;
        
        // Getters and Setters
        public boolean isAutoStart() { return autoStart; }
        public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
        
        public int getThreadPoolSize() { return threadPoolSize; }
        public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
        
        public int getQueueCapacity() { return queueCapacity; }
        public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
        
        public boolean isEnableMetrics() { return enableMetrics; }
        public void setEnableMetrics(boolean enableMetrics) { this.enableMetrics = enableMetrics; }
    }
}
