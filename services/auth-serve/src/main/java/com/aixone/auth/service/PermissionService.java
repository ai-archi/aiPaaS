package com.aixone.auth.service;

import java.util.Map;

public interface PermissionService {
    boolean hasRolePermission(String userId, String resource, String action);
    boolean evaluateAbac(String userId, String resource, String action, Map<String, Object> context);
    boolean checkAccess(String userId, String resource, String action, Map<String, Object> context);
} 