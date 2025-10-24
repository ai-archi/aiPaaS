package com.aixone.event.constant;

/**
 * 事件类型常量
 * 基于事件中心的EventType设计，提供常用的事件类型定义
 */
public class EventType {
    
    // 用户相关事件
    public static final String USER_LOGIN = "user.login";
    public static final String USER_LOGOUT = "user.logout";
    public static final String USER_REGISTER = "user.register";
    public static final String USER_UPDATE = "user.update";
    public static final String USER_DELETE = "user.delete";
    
    // 权限相关事件
    public static final String PERMISSION_GRANT = "permission.grant";
    public static final String PERMISSION_REVOKE = "permission.revoke";
    public static final String ROLE_ASSIGN = "role.assign";
    public static final String ROLE_UNASSIGN = "role.unassign";
    
    // 系统相关事件
    public static final String SYSTEM_START = "system.start";
    public static final String SYSTEM_STOP = "system.stop";
    public static final String SYSTEM_ERROR = "system.error";
    public static final String SYSTEM_WARNING = "system.warning";
    
    // 业务相关事件
    public static final String BUSINESS_CREATE = "business.create";
    public static final String BUSINESS_UPDATE = "business.update";
    public static final String BUSINESS_DELETE = "business.delete";
    public static final String BUSINESS_APPROVE = "business.approve";
    public static final String BUSINESS_REJECT = "business.reject";
    
    // 审计相关事件
    public static final String AUDIT_LOGIN = "audit.login";
    public static final String AUDIT_LOGOUT = "audit.logout";
    public static final String AUDIT_ACCESS = "audit.access";
    public static final String AUDIT_OPERATION = "audit.operation";
    
    // 通知相关事件
    public static final String NOTIFICATION_SEND = "notification.send";
    public static final String NOTIFICATION_READ = "notification.read";
    public static final String NOTIFICATION_DELETE = "notification.delete";
    
    // 数据相关事件
    public static final String DATA_CREATE = "data.create";
    public static final String DATA_UPDATE = "data.update";
    public static final String DATA_DELETE = "data.delete";
    public static final String DATA_EXPORT = "data.export";
    public static final String DATA_IMPORT = "data.import";
    
    // 其他事件
    public static final String OTHER = "other";
    
    /**
     * 验证事件类型是否有效
     */
    public static boolean isValid(String eventType) {
        return eventType != null && !eventType.trim().isEmpty();
    }
    
    /**
     * 获取事件类型的分类
     */
    public static String getCategory(String eventType) {
        if (eventType == null) return "unknown";
        
        if (eventType.startsWith("user.")) return "user";
        if (eventType.startsWith("permission.") || eventType.startsWith("role.")) return "permission";
        if (eventType.startsWith("system.")) return "system";
        if (eventType.startsWith("business.")) return "business";
        if (eventType.startsWith("audit.")) return "audit";
        if (eventType.startsWith("notification.")) return "notification";
        if (eventType.startsWith("data.")) return "data";
        
        return "other";
    }
} 