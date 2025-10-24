package com.aixone.tech.auth.authorization.application.dto;

import java.util.Map;

/**
 * 权限校验请求DTO
 */
public class CheckPermissionRequest {
    private String tenantId;
    private String userId;
    private String resource;
    private String action;
    private Map<String, Object> context;

    public CheckPermissionRequest() {}

    public CheckPermissionRequest(String tenantId, String userId, String resource, String action, Map<String, Object> context) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.resource = resource;
        this.action = action;
        this.context = context;
    }

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
