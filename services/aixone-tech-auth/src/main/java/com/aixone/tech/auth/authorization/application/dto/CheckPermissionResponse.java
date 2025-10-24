package com.aixone.tech.auth.authorization.application.dto;

/**
 * 权限校验响应DTO
 */
public class CheckPermissionResponse {
    private boolean allowed;
    private String message;

    public CheckPermissionResponse() {}

    public CheckPermissionResponse(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    // Getters and Setters
    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
