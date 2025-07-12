package com.aixone.permission.model;

import java.util.Map;

/**
 * 用户模型
 * 代表系统中的用户，支持ABAC属性扩展
 */
public class User {
    /** 用户唯一标识 */
    private String userId;
    /** 用户属性（如部门、职位等） */
    private Map<String, Object> attributes;

    public User() {}
    public User(String userId, Map<String, Object> attributes) {
        this.userId = userId;
        this.attributes = attributes;
    }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
} 