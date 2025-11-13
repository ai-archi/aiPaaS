package com.aixone.eventcenter.event.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Subscription 领域模型单元测试
 */
@DisplayName("Subscription 领域模型测试")
class SubscriptionTest {

    private Subscription subscription;
    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SERVICE = "notification-service";
    private final String TEST_ENDPOINT = "http://localhost:8080/api/events";

    @BeforeEach
    void setUp() {
        subscription = new Subscription(TEST_TENANT_ID, TEST_EVENT_TYPE, TEST_SERVICE, TEST_ENDPOINT);
    }

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建订阅对象")
        void shouldCreateSubscriptionCorrectly() {
            // Then
            assertNotNull(subscription);
            assertEquals(TEST_TENANT_ID, subscription.getTenantId());
            assertEquals(TEST_EVENT_TYPE, subscription.getEventType());
            assertEquals(TEST_SERVICE, subscription.getSubscriberService());
            assertEquals(TEST_ENDPOINT, subscription.getSubscriberEndpoint());
            assertEquals(Subscription.SubscriptionStatus.ACTIVE, subscription.getStatus());
            assertNotNull(subscription.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("应该能够激活订阅")
        void shouldActivateSubscription() {
            // Given
            subscription.deactivate();

            // When
            subscription.activate();

            // Then
            assertEquals(Subscription.SubscriptionStatus.ACTIVE, subscription.getStatus());
            assertNotNull(subscription.getUpdatedAt());
        }

        @Test
        @DisplayName("应该能够停用订阅")
        void shouldDeactivateSubscription() {
            // When
            subscription.deactivate();

            // Then
            assertEquals(Subscription.SubscriptionStatus.INACTIVE, subscription.getStatus());
            assertNotNull(subscription.getUpdatedAt());
        }

        @Test
        @DisplayName("应该能够取消订阅")
        void shouldCancelSubscription() {
            // When
            subscription.cancel();

            // Then
            assertEquals(Subscription.SubscriptionStatus.CANCELLED, subscription.getStatus());
            assertNotNull(subscription.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("配置更新测试")
    class ConfigUpdateTests {

        @Test
        @DisplayName("应该能够更新订阅配置")
        void shouldUpdateConfig() {
            // Given
            String filterConfig = "{\"userId\":\"123\"}";
            String retryConfig = "{\"maxRetries\":3}";

            // When
            subscription.updateConfig(filterConfig, retryConfig);

            // Then
            assertEquals(filterConfig, subscription.getFilterConfig());
            assertEquals(retryConfig, subscription.getRetryConfig());
            assertNotNull(subscription.getUpdatedAt());
        }

        @Test
        @DisplayName("应该能够更新订阅端点")
        void shouldUpdateEndpoint() {
            // Given
            String newEndpoint = "http://localhost:8081/api/events";

            // When
            subscription.updateEndpoint(newEndpoint);

            // Then
            assertEquals(newEndpoint, subscription.getSubscriberEndpoint());
            assertNotNull(subscription.getUpdatedAt());
        }
    }
}

