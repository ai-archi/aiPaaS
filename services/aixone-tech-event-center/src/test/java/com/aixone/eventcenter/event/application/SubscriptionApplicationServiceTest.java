package com.aixone.eventcenter.event.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.domain.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubscriptionApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionApplicationService 应用服务测试")
class SubscriptionApplicationServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    private SubscriptionApplicationService subscriptionApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SERVICE = "notification-service";
    private final String TEST_ENDPOINT = "http://localhost:8080/api/events";

    @BeforeEach
    void setUp() {
        subscriptionApplicationService = new SubscriptionApplicationService();
        try {
            java.lang.reflect.Field repositoryField = SubscriptionApplicationService.class.getDeclaredField("subscriptionRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(subscriptionApplicationService, subscriptionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up SubscriptionApplicationService dependencies", e);
        }
    }

    @Nested
    @DisplayName("创建订阅测试")
    class CreateSubscriptionTests {

        @Test
        @DisplayName("应该成功创建订阅")
        void shouldCreateSubscriptionSuccessfully() {
            // Given
            Subscription savedSubscription = createValidSubscription();
            savedSubscription.setSubscriptionId(1L);
            
            when(subscriptionRepository.findByTenantIdAndEventType(TEST_TENANT_ID, TEST_EVENT_TYPE))
                    .thenReturn(List.of());
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);

            // When
            Subscription result = subscriptionApplicationService.createSubscription(
                    TEST_TENANT_ID, TEST_EVENT_TYPE, TEST_SERVICE, TEST_ENDPOINT, null, null);

            // Then
            assertNotNull(result);
            assertEquals(savedSubscription, result);
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("应该拒绝创建重复的活跃订阅")
        void shouldRejectDuplicateActiveSubscription() {
            // Given
            Subscription existing = createValidSubscription();
            existing.setSubscriptionId(1L);
            when(subscriptionRepository.findByTenantIdAndEventType(TEST_TENANT_ID, TEST_EVENT_TYPE))
                    .thenReturn(List.of(existing));

            // When & Then
            BizException exception = assertThrows(BizException.class, () -> {
                subscriptionApplicationService.createSubscription(
                        TEST_TENANT_ID, TEST_EVENT_TYPE, TEST_SERVICE, TEST_ENDPOINT, null, null);
            });
            assertEquals("SUBSCRIPTION_ALREADY_EXISTS", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("更新订阅测试")
    class UpdateSubscriptionTests {

        @Test
        @DisplayName("应该成功更新订阅")
        void shouldUpdateSubscriptionSuccessfully() {
            // Given
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(1L);
            
            when(subscriptionRepository.findBySubscriptionIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

            // When
            Subscription result = subscriptionApplicationService.updateSubscription(
                    1L, TEST_TENANT_ID, "http://new-endpoint", null, null);

            // Then
            assertNotNull(result);
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("订阅不存在应该抛出异常")
        void shouldThrowExceptionWhenSubscriptionNotFound() {
            // Given
            when(subscriptionRepository.findBySubscriptionIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            BizException exception = assertThrows(BizException.class, () -> {
                subscriptionApplicationService.updateSubscription(1L, TEST_TENANT_ID, null, null, null);
            });
            assertEquals("SUBSCRIPTION_NOT_FOUND", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("应该成功激活订阅")
        void shouldActivateSubscriptionSuccessfully() {
            // Given
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(1L);
            subscription.deactivate();
            
            when(subscriptionRepository.findBySubscriptionIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

            // When
            Subscription result = subscriptionApplicationService.activateSubscription(1L, TEST_TENANT_ID);

            // Then
            assertEquals(Subscription.SubscriptionStatus.ACTIVE, result.getStatus());
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("应该成功取消订阅")
        void shouldCancelSubscriptionSuccessfully() {
            // Given
            Subscription subscription = createValidSubscription();
            subscription.setSubscriptionId(1L);
            
            when(subscriptionRepository.findBySubscriptionIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

            // When
            subscriptionApplicationService.cancelSubscription(1L, TEST_TENANT_ID);

            // Then
            verify(subscriptionRepository).save(any(Subscription.class));
        }
    }

    @Nested
    @DisplayName("查询测试")
    class QueryTests {

        @Test
        @DisplayName("应该成功查询租户的所有订阅")
        void shouldGetSubscriptionsByTenantSuccessfully() {
            // Given
            List<Subscription> subscriptions = Arrays.asList(createValidSubscription(), createValidSubscription());
            when(subscriptionRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(subscriptions);

            // When
            List<Subscription> result = subscriptionApplicationService.getSubscriptionsByTenant(TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(subscriptionRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功获取事件类型的活跃订阅")
        void shouldGetActiveSubscriptionsByEventTypeSuccessfully() {
            // Given
            List<Subscription> subscriptions = Arrays.asList(createValidSubscription());
            when(subscriptionRepository.findByEventTypeAndStatus(
                    TEST_EVENT_TYPE, Subscription.SubscriptionStatus.ACTIVE))
                    .thenReturn(subscriptions);

            // When
            List<Subscription> result = subscriptionApplicationService.getActiveSubscriptionsByEventType(TEST_EVENT_TYPE);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(subscriptionRepository).findByEventTypeAndStatus(
                    TEST_EVENT_TYPE, Subscription.SubscriptionStatus.ACTIVE);
        }
    }

    private Subscription createValidSubscription() {
        return new Subscription(TEST_TENANT_ID, TEST_EVENT_TYPE, TEST_SERVICE, TEST_ENDPOINT);
    }
}

