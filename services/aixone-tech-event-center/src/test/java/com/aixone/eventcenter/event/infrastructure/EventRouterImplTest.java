package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EventRouterImpl 基础设施测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventRouterImpl 基础设施测试")
class EventRouterImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    private EventRouterImpl eventRouter;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";

    @BeforeEach
    void setUp() {
        eventRouter = new EventRouterImpl();
        try {
            java.lang.reflect.Field repositoryField = EventRouterImpl.class.getDeclaredField("subscriptionRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(eventRouter, subscriptionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up EventRouterImpl dependencies", e);
        }
    }

    @Nested
    @DisplayName("路由订阅测试")
    class RouteSubscriptionsTests {

        @Test
        @DisplayName("应该成功路由订阅")
        void shouldRouteSubscriptionsSuccessfully() {
            // Given
            Subscription subscription = createValidSubscription();
            List<Subscription> subscriptions = Arrays.asList(subscription);
            
            when(subscriptionRepository.findByEventTypeAndStatus(
                    TEST_EVENT_TYPE, Subscription.SubscriptionStatus.ACTIVE))
                    .thenReturn(subscriptions);

            // When
            List<Subscription> result = eventRouter.routeSubscriptions(TEST_EVENT_TYPE);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(subscriptionRepository).findByEventTypeAndStatus(
                    TEST_EVENT_TYPE, Subscription.SubscriptionStatus.ACTIVE);
        }

        @Test
        @DisplayName("没有订阅应该返回空列表")
        void shouldReturnEmptyListWhenNoSubscriptions() {
            // Given
            when(subscriptionRepository.findByEventTypeAndStatus(
                    TEST_EVENT_TYPE, Subscription.SubscriptionStatus.ACTIVE))
                    .thenReturn(List.of());

            // When
            List<Subscription> result = eventRouter.routeSubscriptions(TEST_EVENT_TYPE);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("过滤匹配测试")
    class FilterMatchingTests {

        @Test
        @DisplayName("没有过滤配置应该匹配所有事件")
        void shouldMatchAllEventsWhenNoFilterConfig() {
            // Given
            Event event = createValidEvent();
            Subscription subscription = createValidSubscription();
            subscription.setFilterConfig(null);

            // When
            boolean result = eventRouter.matchesFilter(event, subscription);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该匹配符合过滤条件的事件")
        void shouldMatchEventWithMatchingFilter() {
            // Given
            Event event = createValidEvent();
            Subscription subscription = createValidSubscription();
            subscription.setFilterConfig("{\"userId\":\"123\"}");

            // When
            boolean result = eventRouter.matchesFilter(event, subscription);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该拒绝不符合过滤条件的事件")
        void shouldRejectEventWithNonMatchingFilter() {
            // Given
            Event event = createValidEvent();
            Subscription subscription = createValidSubscription();
            subscription.setFilterConfig("{\"userId\":\"999\"}");

            // When
            boolean result = eventRouter.matchesFilter(event, subscription);

            // Then
            assertFalse(result);
        }
    }

    private Event createValidEvent() {
        Event event = new Event(TEST_EVENT_TYPE, "test-source", "{\"userId\":\"123\"}", TEST_TENANT_ID);
        return event;
    }

    private Subscription createValidSubscription() {
        return new Subscription(TEST_TENANT_ID, TEST_EVENT_TYPE, "test-service", "http://localhost:8080/api/events");
    }
}

