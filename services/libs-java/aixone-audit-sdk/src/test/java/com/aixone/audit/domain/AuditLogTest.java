package com.aixone.audit.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * AuditLog 领域模型测试
 */
class AuditLogTest {

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog(1L, "user123", "LOGIN", "auth", "SUCCESS");
    }

    @Test
    void testAuditLogCreation() {
        assertNotNull(auditLog);
        assertEquals(1L, auditLog.getId());
        assertEquals("user123", auditLog.getUserId());
        assertEquals("LOGIN", auditLog.getAction());
        assertEquals("auth", auditLog.getResource());
        assertEquals("SUCCESS", auditLog.getResult());
        assertNotNull(auditLog.getTimestamp());
    }

    @Test
    void testAuditLogWithFullParameters() {
        AuditLog fullAuditLog = new AuditLog(2L, "user456", "LOGOUT", "auth", "SUCCESS", 
                "192.168.1.100", "Mozilla/5.0", "User logged out successfully");
        
        assertNotNull(fullAuditLog);
        assertEquals(2L, fullAuditLog.getId());
        assertEquals("user456", fullAuditLog.getUserId());
        assertEquals("LOGOUT", fullAuditLog.getAction());
        assertEquals("auth", fullAuditLog.getResource());
        assertEquals("SUCCESS", fullAuditLog.getResult());
        assertEquals("192.168.1.100", fullAuditLog.getClientIp());
        assertEquals("Mozilla/5.0", fullAuditLog.getUserAgent());
        assertEquals("User logged out successfully", fullAuditLog.getDetails());
    }

    @Test
    void testAuditLogFromEntity() {
        LocalDateTime now = LocalDateTime.now();
        AuditLog entityAuditLog = new AuditLog(3L, "tenant1", "user789", "PERMISSION_CHECK", 
                "user:read", "SUCCESS", now, "10.0.0.1", "Chrome/91.0", 
                "Permission granted", null, "session123");
        
        assertNotNull(entityAuditLog);
        assertEquals(3L, entityAuditLog.getId());
        assertEquals("tenant1", entityAuditLog.getTenantId());
        assertEquals("user789", entityAuditLog.getUserId());
        assertEquals("PERMISSION_CHECK", entityAuditLog.getAction());
        assertEquals("user:read", entityAuditLog.getResource());
        assertEquals("SUCCESS", entityAuditLog.getResult());
        assertEquals(now, entityAuditLog.getTimestamp());
        assertEquals("10.0.0.1", entityAuditLog.getClientIp());
        assertEquals("Chrome/91.0", entityAuditLog.getUserAgent());
        assertEquals("Permission granted", entityAuditLog.getDetails());
        assertEquals("session123", entityAuditLog.getSessionId());
    }

    @Test
    void testIsSuccess() {
        assertTrue(auditLog.isSuccess());
        
        AuditLog failureLog = new AuditLog(4L, "user123", "LOGIN", "auth", "FAILURE");
        assertFalse(failureLog.isSuccess());
    }

    @Test
    void testIsFailure() {
        assertFalse(auditLog.isFailure());
        
        AuditLog failureLog = new AuditLog(5L, "user123", "LOGIN", "auth", "FAILURE");
        assertTrue(failureLog.isFailure());
    }

    @Test
    void testBelongsToUser() {
        assertTrue(auditLog.belongsToUser("user123"));
        assertFalse(auditLog.belongsToUser("user456"));
        assertFalse(auditLog.belongsToUser(null));
    }

    @Test
    void testIsLoginAction() {
        assertTrue(auditLog.isLoginAction());
        
        AuditLog logoutLog = new AuditLog(6L, "user123", "LOGOUT", "auth", "SUCCESS");
        assertFalse(logoutLog.isLoginAction());
    }

    @Test
    void testIsLogoutAction() {
        assertFalse(auditLog.isLogoutAction());
        
        AuditLog logoutLog = new AuditLog(7L, "user123", "LOGOUT", "auth", "SUCCESS");
        assertTrue(logoutLog.isLogoutAction());
    }

    @Test
    void testIsPermissionCheckAction() {
        assertFalse(auditLog.isPermissionCheckAction());
        
        AuditLog permissionLog = new AuditLog(8L, "user123", "PERMISSION_CHECK", "user:read", "SUCCESS");
        assertTrue(permissionLog.isPermissionCheckAction());
    }

    @Test
    void testSetDetails() {
        auditLog.setDetails("Custom details");
        assertEquals("Custom details", auditLog.getDetails());
    }

    @Test
    void testSetErrorMessage() {
        auditLog.setErrorMessage("Invalid credentials");
        assertEquals("Invalid credentials", auditLog.getErrorMessage());
    }

    @Test
    void testSetSessionId() {
        auditLog.setSessionId("session456");
        assertEquals("session456", auditLog.getSessionId());
    }
}