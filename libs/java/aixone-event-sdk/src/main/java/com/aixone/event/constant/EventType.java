package com.aixone.event.constant;

/**
 * 事件类型枚举
 */
public enum EventType {
    LOGIN("login", "用户登录"),
    LOGOUT("logout", "用户登出"),
    PERMISSION_CHANGE("permission_change", "权限变更"),
    TASK_EXECUTE("task_execute", "任务执行"),
    AUDIT("audit", "审计事件"),
    OTHER("other", "其他");

    private final String code;
    private final String desc;

    EventType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() { return code; }
    public String getDesc() { return desc; }
} 