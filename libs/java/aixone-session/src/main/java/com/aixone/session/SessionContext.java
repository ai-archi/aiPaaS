package com.aixone.session;

import java.util.Set;

/**
 * 统一上下文，ThreadLocal 保存当前请求的用户、租户、角色、ABAC属性等
 */
public class SessionContext {
    private static final ThreadLocal<SessionInfo> CONTEXT = new ThreadLocal<>();

    public static void set(SessionInfo info) {
        CONTEXT.set(info);
    }
    public static SessionInfo get() {
        return CONTEXT.get();
    }
    public static void clear() {
        CONTEXT.remove();
    }
    public static String getUserId() {
        return get() != null ? get().getUserId() : null;
    }
    public static String getTenantId() {
        return get() != null ? get().getTenantId() : null;
    }
    public static Set<String> getRoles() {
        return get() != null ? get().getRoles() : null;
    }
    public static AbacAttributes getAbacAttributes() {
        return get() != null ? get().getAbacAttributes() : null;
    }

    /**
     * SessionInfo 内部类，保存所有上下文属性
     */
    public static class SessionInfo {
        private String userId;
        private String tenantId;
        private Set<String> roles;
        private AbacAttributes abacAttributes;
        // getter/setter
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }
        public AbacAttributes getAbacAttributes() { return abacAttributes; }
        public void setAbacAttributes(AbacAttributes abacAttributes) { this.abacAttributes = abacAttributes; }
    }
} 