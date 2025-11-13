package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.session.SessionContext;
import com.aixone.eventcenter.event.application.SubscriptionApplicationService;
import com.aixone.eventcenter.event.domain.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubscriptionController 控制器测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionController 控制器测试")
class SubscriptionControllerTest {

    @Mock
    private SubscriptionApplicationService subscriptionApplicationService;

    private SubscriptionController subscriptionController;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SUBSCRIBER_SERVICE = "notification-service";
    private final String TEST_SUBSCRIBER_ENDPOINT = "http://localhost:8082/api/v1/notifications/events";
    private final String TEST_FILTER_CONFIG = "{\"userId\":{\"$eq\":\"123\"}}";

    @BeforeEach
    void setUp() {
        subscriptionController = new SubscriptionController();
        try {
            java.lang.reflect.Field serviceField = SubscriptionController.class.getDeclaredField("subscriptionApplicationService");
            serviceField.setAccessible(true);
            serviceField.set(subscriptionController, subscriptionApplicationService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up SubscriptionController dependencies", e);
        }
    }

    @Nested
    @DisplayName("创建订阅测试")
    class CreateSubscriptionTests {

        @Test
        @DisplayName("应该成功创建订阅")
        void shouldCreateSubscriptionSuccessfully() {
            // Given
            SubscriptionController.CreateSubscriptionRequest request = new SubscriptionController.CreateSubscriptionRequest();
            request.setEventType(TEST_EVENT_TYPE);
            request.setSubscriberService(TEST_SUBSCRIBER_SERVICE);
            request.setSubscriberEndpoint(TEST_SUBSCRIBER_ENDPOINT);
            request.setFilterConfig(TEST_FILTER_CONFIG);

            Subscription savedSubscription = createValidSubscription();
            savedSubscription.setSubscriptionId(1L);

            when(subscriptionApplicationService.createSubscription(
                    eq(TEST_TENANT_ID), eq(TEST_EVENT_TYPE), eq(TEST_SUBSCRIBER_SERVICE), 
                    eq(TEST_SUBSCRIBER_ENDPOINT), eq(TEST_FILTER_CONFIG), isNull()))
                    .thenReturn(savedSubscription);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.createSubscription(request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(savedSubscription, result.getData());
                verify(subscriptionApplicationService).createSubscription(
                        TEST_TENANT_ID, TEST_EVENT_TYPE, TEST_SUBSCRIBER_SERVICE, 
                        TEST_SUBSCRIBER_ENDPOINT, TEST_FILTER_CONFIG, null);
            }
        }

        @Test
        @DisplayName("缺少租户ID应该返回错误")
        void missingTenantIdShouldReturnError() {
            // Given
            SubscriptionController.CreateSubscriptionCommand command = new SubscriptionController.CreateSubscriptionCommand();

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);

                ApiResponse<Subscription> result = subscriptionController.createSubscription(command);

                // Then
                assertNotNull(result);
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
                verify(subscriptionApplicationService, never()).createSubscription(any(), any(), any(), any(), any());
            }
        }
    }

    @Nested
    @DisplayName("查询订阅测试")
    class QuerySubscriptionTests {

        @Test
        @DisplayName("应该成功获取订阅列表")
        void shouldGetSubscriptionsSuccessfully() {
            // Given
            List<Subscription> subscriptions = Arrays.asList(createValidSubscription(), createValidSubscription());
            when(subscriptionApplicationService.getSubscriptionsByTenant(TEST_TENANT_ID)).thenReturn(subscriptions);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<List<Subscription>> result = subscriptionController.getSubscriptions();

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(subscriptionApplicationService).getSubscriptionsByTenant(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功获取订阅详情")
        void shouldGetSubscriptionByIdSuccessfully() {
            // Given
            Long subscriptionId = 1L;
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(subscriptionId);

            when(subscriptionApplicationService.getSubscriptionById(subscriptionId, TEST_TENANT_ID))
                    .thenReturn(Optional.of(subscription));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.getSubscriptionById(subscriptionId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(subscription, result.getData());
                verify(subscriptionApplicationService).getSubscriptionById(subscriptionId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("订阅不存在应该返回错误")
        void nonExistentSubscriptionShouldReturnError() {
            // Given
            Long subscriptionId = 999L;
            when(subscriptionApplicationService.getSubscriptionById(subscriptionId, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.getSubscriptionById(subscriptionId);

                // Then
                assertNotNull(result);
                assertEquals(40401, result.getCode());
                assertEquals("订阅不存在", result.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("更新订阅测试")
    class UpdateSubscriptionTests {

        @Test
        @DisplayName("应该成功更新订阅")
        void shouldUpdateSubscriptionSuccessfully() {
            // Given
            Long subscriptionId = 1L;
            SubscriptionController.UpdateSubscriptionRequest request = new SubscriptionController.UpdateSubscriptionRequest();
            request.setSubscriberEndpoint(TEST_SUBSCRIBER_ENDPOINT);
            request.setFilterConfig(TEST_FILTER_CONFIG);

            Subscription updatedSubscription = createValidSubscription();
            updatedSubscription.setSubscriptionId(subscriptionId);

            when(subscriptionApplicationService.updateSubscription(
                    eq(subscriptionId), eq(TEST_TENANT_ID), eq(TEST_SUBSCRIBER_ENDPOINT), 
                    eq(TEST_FILTER_CONFIG), isNull()))
                    .thenReturn(updatedSubscription);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.updateSubscription(subscriptionId, request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(updatedSubscription, result.getData());
                verify(subscriptionApplicationService).updateSubscription(
                        subscriptionId, TEST_TENANT_ID, TEST_SUBSCRIBER_ENDPOINT, 
                        TEST_FILTER_CONFIG, null);
            }
        }
    }

    @Nested
    @DisplayName("激活/停用/取消订阅测试")
    class StatusChangeTests {

        @Test
        @DisplayName("应该成功激活订阅")
        void shouldActivateSubscriptionSuccessfully() {
            // Given
            Long subscriptionId = 1L;
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(subscriptionId);

            when(subscriptionApplicationService.activateSubscription(subscriptionId, TEST_TENANT_ID))
                    .thenReturn(subscription);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.activateSubscription(subscriptionId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                verify(subscriptionApplicationService).activateSubscription(subscriptionId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功停用订阅")
        void shouldDeactivateSubscriptionSuccessfully() {
            // Given
            Long subscriptionId = 1L;
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(subscriptionId);

            when(subscriptionApplicationService.deactivateSubscription(subscriptionId, TEST_TENANT_ID))
                    .thenReturn(subscription);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Subscription> result = subscriptionController.deactivateSubscription(subscriptionId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                verify(subscriptionApplicationService).deactivateSubscription(subscriptionId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功取消订阅")
        void shouldCancelSubscriptionSuccessfully() {
            // Given
            Long subscriptionId = 1L;
            doNothing().when(subscriptionApplicationService).cancelSubscription(subscriptionId, TEST_TENANT_ID);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Void> result = subscriptionController.cancelSubscription(subscriptionId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                verify(subscriptionApplicationService).cancelSubscription(subscriptionId, TEST_TENANT_ID);
            }
        }
    }

    private Subscription createValidSubscription() {
        Subscription subscription = new Subscription(TEST_TENANT_ID, TEST_EVENT_TYPE, 
                TEST_SUBSCRIBER_SERVICE, TEST_SUBSCRIBER_ENDPOINT);
        subscription.setFilterConfig(TEST_FILTER_CONFIG);
        return subscription;
    }
}

