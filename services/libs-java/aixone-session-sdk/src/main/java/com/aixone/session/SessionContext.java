package com.aixone.session;

import java.util.Set;
import java.util.Date;

/**
 * 统一上下文，ThreadLocal 保存当前请求的用户、租户、角色、权限、ABAC属性等
 * 为所有微服务提供统一的会话上下文管理
 */
public class SessionContext {
    private static final ThreadLocal<SessionInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前会话信息
     */
    public static void set(SessionInfo info) {
        CONTEXT.set(info);
    }
    
    /**
     * 获取当前会话信息
     */
    public static SessionInfo get() {
        return CONTEXT.get();
    }
    
    /**
     * 清除当前会话信息
     */
    public static void clear() {
        CONTEXT.remove();
    }
    
    /**
     * 检查是否有有效的会话
     */
    public static boolean hasSession() {
        return get() != null;
    }
    
    // 便捷方法
    public static String getUserId() {
        return get() != null ? get().getUserId() : null;
    }
    
    public static String getTenantId() {
        return get() != null ? get().getTenantId() : null;
    }
    
    public static String getClientId() {
        return get() != null ? get().getClientId() : null;
    }
    
    public static AbacAttributes getAbacAttributes() {
        return get() != null ? get().getAbacAttributes() : null;
    }
    
    public static String getTokenType() {
        return get() != null ? get().getTokenType() : null;
    }
    
    /**
     * 获取 ABAC 属性值
     */
    public static Object getAbacAttribute(String key) {
        AbacAttributes abac = getAbacAttributes();
        return abac != null ? abac.get(key) : null;
    }
    
    /**
     * 获取 ABAC 属性值（带默认值）
     */
    public static Object getAbacAttribute(String key, Object defaultValue) {
        Object value = getAbacAttribute(key);
        return value != null ? value : defaultValue;
    }

    /**
     * SessionInfo 内部类，保存所有上下文属性
     */
    public static class SessionInfo {
        private String userId;
        private String tenantId;
        private String clientId;
        private AbacAttributes abacAttributes;
        private String tokenType;
        private Date issuedAt;
        private Date expiresAt;
        
        // 构造函数
        public SessionInfo() {}
        
        public SessionInfo(String userId, String tenantId) {
            this.userId = userId;
            this.tenantId = tenantId;
        }
        
        // getter/setter
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        
        public AbacAttributes getAbacAttributes() { return abacAttributes; }
        public void setAbacAttributes(AbacAttributes abacAttributes) { this.abacAttributes = abacAttributes; }
        
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        
        public Date getIssuedAt() { return issuedAt; }
        public void setIssuedAt(Date issuedAt) { this.issuedAt = issuedAt; }
        
        public Date getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
        
        /**
         * 检查会话是否过期
         */
        public boolean isExpired() {
            return expiresAt != null && new Date().after(expiresAt);
        }
        
        /**
         * 检查是否为访问令牌
         */
        public boolean isAccessToken() {
            return "ACCESS".equals(tokenType);
        }
        
        /**
         * 检查是否为刷新令牌
         */
        public boolean isRefreshToken() {
            return "REFRESH".equals(tokenType);
        }
        
        @Override
        public String toString() {
            return "SessionInfo{" +
                    "userId='" + userId + '\'' +
                    ", tenantId='" + tenantId + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    '}';
        }
    }
} 