package com.aixone.tech.auth.authorization.application.service;

import com.aixone.tech.auth.authorization.application.dto.CreateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.application.dto.AbacPolicyResponse;
import com.aixone.tech.auth.authorization.application.dto.UpdateAbacPolicyRequest;
import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ABAC策略管理应用服务测试
 */
@ExtendWith(MockitoExtension.class)
class AbacPolicyManagementApplicationServiceTest {

    @Mock
    private AbacPolicyRepository abacPolicyRepository;

    @InjectMocks
    private AbacPolicyManagementApplicationService abacPolicyManagementApplicationService;

    private String tenantId;
    private String policyId;
    private AbacPolicy testPolicy;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant";
        policyId = UUID.randomUUID().toString();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("timezone", "Asia/Shanghai");
        attributes.put("enabled", true);

        testPolicy = new AbacPolicy(
            policyId,
            tenantId,
            "Test Policy",
            "Test policy description",
            "user:profile",
            "read",
            "time >= 09:00 AND time <= 18:00",
            attributes
        );
        testPolicy.setCreatedAt(LocalDateTime.now());
        testPolicy.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateAbacPolicy_Success() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("timezone", "Asia/Shanghai");
        attributes.put("enabled", true);

        CreateAbacPolicyRequest request = new CreateAbacPolicyRequest(
            tenantId,
            "New Policy",
            "New policy description",
            "user:profile",
            "write",
            "user.level >= 3",
            attributes
        );

        when(abacPolicyRepository.existsByTenantIdAndName(tenantId, "New Policy")).thenReturn(false);
        when(abacPolicyRepository.save(any(AbacPolicy.class))).thenReturn(testPolicy);

        // Act
        AbacPolicyResponse response = abacPolicyManagementApplicationService.createAbacPolicy(request);

        // Assert
        assertNotNull(response);
        assertEquals(policyId, response.getPolicyId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("Test Policy", response.getName());
        assertEquals("Test policy description", response.getDescription());
        assertEquals("user:profile", response.getResource());
        assertEquals("read", response.getAction());
        assertEquals("time >= 09:00 AND time <= 18:00", response.getCondition());
        assertEquals(attributes, response.getAttributes());

        verify(abacPolicyRepository).existsByTenantIdAndName(tenantId, "New Policy");
        verify(abacPolicyRepository).save(any(AbacPolicy.class));
    }

    @Test
    void testCreateAbacPolicy_DuplicateName_ThrowsException() {
        // Arrange
        CreateAbacPolicyRequest request = new CreateAbacPolicyRequest(
            tenantId,
            "Test Policy",
            "Test policy description",
            "user:profile",
            "write",
            "user.level >= 3",
            null
        );

        when(abacPolicyRepository.existsByTenantIdAndName(tenantId, "Test Policy")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            abacPolicyManagementApplicationService.createAbacPolicy(request);
        });

        verify(abacPolicyRepository).existsByTenantIdAndName(tenantId, "Test Policy");
        verify(abacPolicyRepository, never()).save(any(AbacPolicy.class));
    }

    @Test
    void testUpdateAbacPolicy_Success() {
        // Arrange
        Map<String, Object> newAttributes = new HashMap<>();
        newAttributes.put("timezone", "UTC");
        newAttributes.put("enabled", false);

        UpdateAbacPolicyRequest request = new UpdateAbacPolicyRequest(
            tenantId,
            "Updated Policy",
            "Updated policy description",
            "user:profile",
            "write",
            "user.level >= 5",
            newAttributes
        );

        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(testPolicy);
        when(abacPolicyRepository.existsByTenantIdAndName(tenantId, "Updated Policy")).thenReturn(false);
        when(abacPolicyRepository.save(any(AbacPolicy.class))).thenReturn(testPolicy);

        // Act
        AbacPolicyResponse response = abacPolicyManagementApplicationService.updateAbacPolicy(policyId, request);

        // Assert
        assertNotNull(response);
        assertEquals(policyId, response.getPolicyId());
        assertEquals("Updated Policy", response.getName());
        assertEquals("Updated policy description", response.getDescription());
        assertEquals("user:profile", response.getResource());
        assertEquals("write", response.getAction());
        assertEquals("user.level >= 5", response.getCondition());
        assertEquals(newAttributes, response.getAttributes());

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
        verify(abacPolicyRepository).existsByTenantIdAndName(tenantId, "Updated Policy");
        verify(abacPolicyRepository).save(any(AbacPolicy.class));
    }

    @Test
    void testUpdateAbacPolicy_PolicyNotFound_ThrowsException() {
        // Arrange
        UpdateAbacPolicyRequest request = new UpdateAbacPolicyRequest(
            tenantId,
            "Updated Policy",
            "Updated policy description",
            "user:profile",
            "write",
            "user.level >= 5",
            null
        );

        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            abacPolicyManagementApplicationService.updateAbacPolicy(policyId, request);
        });

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
        verify(abacPolicyRepository, never()).existsByTenantIdAndName(anyString(), anyString());
        verify(abacPolicyRepository, never()).save(any(AbacPolicy.class));
    }

    @Test
    void testUpdateAbacPolicy_DuplicateName_ThrowsException() {
        // Arrange
        UpdateAbacPolicyRequest request = new UpdateAbacPolicyRequest(
            tenantId,
            "Existing Policy", // Try to change to an existing name
            "Updated policy description",
            "user:profile",
            "write",
            "user.level >= 5",
            null
        );

        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(testPolicy);
        when(abacPolicyRepository.existsByTenantIdAndName(tenantId, "Existing Policy")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            abacPolicyManagementApplicationService.updateAbacPolicy(policyId, request);
        });

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
        verify(abacPolicyRepository).existsByTenantIdAndName(tenantId, "Existing Policy");
        verify(abacPolicyRepository, never()).save(any(AbacPolicy.class));
    }

    @Test
    void testUpdateAbacPolicy_SameName_DoesNotCheckDuplicate() {
        // Arrange
        UpdateAbacPolicyRequest request = new UpdateAbacPolicyRequest(
            tenantId,
            "Test Policy", // Same name as existing policy
            "Updated policy description",
            "user:profile",
            "write",
            "user.level >= 5",
            null
        );

        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(testPolicy);
        when(abacPolicyRepository.save(any(AbacPolicy.class))).thenReturn(testPolicy);

        // Act
        AbacPolicyResponse response = abacPolicyManagementApplicationService.updateAbacPolicy(policyId, request);

        // Assert
        assertNotNull(response);
        assertEquals(policyId, response.getPolicyId());

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
        verify(abacPolicyRepository, never()).existsByTenantIdAndName(anyString(), anyString());
        verify(abacPolicyRepository).save(any(AbacPolicy.class));
    }

    @Test
    void testDeleteAbacPolicy_Success() {
        // Arrange
        doNothing().when(abacPolicyRepository).deleteByTenantIdAndPolicyId(tenantId, policyId);

        // Act
        abacPolicyManagementApplicationService.deleteAbacPolicy(tenantId, policyId);

        // Assert
        verify(abacPolicyRepository).deleteByTenantIdAndPolicyId(tenantId, policyId);
    }

    @Test
    void testGetAbacPolicyById_Success() {
        // Arrange
        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(testPolicy);

        // Act
        AbacPolicyResponse response = abacPolicyManagementApplicationService.getAbacPolicyById(tenantId, policyId);

        // Assert
        assertNotNull(response);
        assertEquals(policyId, response.getPolicyId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals("Test Policy", response.getName());
        assertEquals("Test policy description", response.getDescription());
        assertEquals("user:profile", response.getResource());
        assertEquals("read", response.getAction());
        assertEquals("time >= 09:00 AND time <= 18:00", response.getCondition());

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
    }

    @Test
    void testGetAbacPolicyById_PolicyNotFound_ThrowsException() {
        // Arrange
        when(abacPolicyRepository.findByTenantIdAndPolicyId(tenantId, policyId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            abacPolicyManagementApplicationService.getAbacPolicyById(tenantId, policyId);
        });

        verify(abacPolicyRepository).findByTenantIdAndPolicyId(tenantId, policyId);
    }

    @Test
    void testGetAllAbacPolicies_Success() {
        // Arrange
        AbacPolicy policy1 = new AbacPolicy(
            UUID.randomUUID().toString(),
            tenantId,
            "Policy 1",
            "Description 1",
            "user:profile",
            "read",
            "time >= 09:00",
            null
        );
        AbacPolicy policy2 = new AbacPolicy(
            UUID.randomUUID().toString(),
            tenantId,
            "Policy 2",
            "Description 2",
            "user:profile",
            "write",
            "user.level >= 3",
            null
        );
        List<AbacPolicy> policies = Arrays.asList(policy1, policy2);

        when(abacPolicyRepository.findByTenantId(tenantId)).thenReturn(policies);

        // Act
        List<AbacPolicyResponse> responses = abacPolicyManagementApplicationService.getAllAbacPolicies(tenantId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Policy 1", responses.get(0).getName());
        assertEquals("Policy 2", responses.get(1).getName());

        verify(abacPolicyRepository).findByTenantId(tenantId);
    }
}
