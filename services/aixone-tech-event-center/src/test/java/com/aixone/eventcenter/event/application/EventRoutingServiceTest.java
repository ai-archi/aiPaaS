package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRouter;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.infrastructure.EventDistributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EventRoutingService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventRoutingService 应用服务测试")
class EventRoutingServiceTest {

    @Mock
    private EventRouter eventRouter;

    @Mock
    private EventDistributor eventDistributor;

    @Mock
    private com.aixone.eventcenter.event.infrastructure.EventDeliveryRecordService deliveryRecordService;

    private EventRoutingService eventRoutingService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";

    @BeforeEach
    void setUp() {
        eventRoutingService = new EventRoutingService();
        try {
            java.lang.reflect.Field routerField = EventRoutingService.class.getDeclaredField("eventRouter");
            routerField.setAccessible(true);
            routerField.set(eventRoutingService, eventRouter);
            
            java.lang.reflect.Field distributorField = EventRoutingService.class.getDeclaredField("eventDistributor");
            distributorField.setAccessible(true);
            distributorField.set(eventRoutingService, eventDistributor);
            
            java.lang.reflect.Field deliveryRecordServiceField = EventRoutingService.class.getDeclaredField("deliveryRecordService");
            deliveryRecordServiceField.setAccessible(true);
            deliveryRecordServiceField.set(eventRoutingService, deliveryRecordService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up EventRoutingService dependencies", e);
        }
    }

    @Test
    @DisplayName("应该成功路由和分发事件")
    void shouldRouteAndDistributeEventSuccessfully() {
        // Given
        Event event = createValidEvent();
        Subscription subscription = createValidSubscription();
        subscription.setSubscriptionId(1L);
        List<Subscription> subscriptions = Arrays.asList(subscription);
        
        when(eventRouter.routeSubscriptions(TEST_EVENT_TYPE)).thenReturn(subscriptions);
        when(eventRouter.matchesFilter(event, subscription)).thenReturn(true);
        when(deliveryRecordService.findOrCreateRecord(anyLong(), anyLong(), anyString(), anyInt()))
                .thenReturn(null); // 不创建记录，简化测试
        when(eventDistributor.distribute(any(Event.class), any(Subscription.class), any()))
                .thenReturn(true);

        // When
        eventRoutingService.routeAndDistribute(event);

        // Then
        verify(eventRouter).routeSubscriptions(TEST_EVENT_TYPE);
        verify(eventRouter).matchesFilter(event, subscription);
        verify(eventDistributor, atLeastOnce()).distribute(any(Event.class), any(Subscription.class), any());
    }

    @Test
    @DisplayName("没有订阅应该跳过分发")
    void shouldSkipDistributionWhenNoSubscriptions() {
        // Given
        Event event = createValidEvent();
        when(eventRouter.routeSubscriptions(TEST_EVENT_TYPE)).thenReturn(List.of());

        // When
        eventRoutingService.routeAndDistribute(event);

        // Then
        verify(eventRouter).routeSubscriptions(TEST_EVENT_TYPE);
        verify(eventDistributor, never()).distribute(any(), any(), any());
    }

    @Test
    @DisplayName("没有匹配的订阅应该跳过分发")
    void shouldSkipDistributionWhenNoMatchingSubscriptions() {
        // Given
        Event event = createValidEvent();
        Subscription subscription = createValidSubscription();
        List<Subscription> subscriptions = Arrays.asList(subscription);
        
        when(eventRouter.routeSubscriptions(TEST_EVENT_TYPE)).thenReturn(subscriptions);
        when(eventRouter.matchesFilter(event, subscription)).thenReturn(false);

        // When
        eventRoutingService.routeAndDistribute(event);

        // Then
        verify(eventRouter).routeSubscriptions(TEST_EVENT_TYPE);
        verify(eventRouter).matchesFilter(event, subscription);
        verify(eventDistributor, never()).distribute(any(), any(), any());
    }

    private Event createValidEvent() {
        Event event = new Event(TEST_EVENT_TYPE, "test-source", "{\"userId\":\"123\"}", TEST_TENANT_ID);
        event.setEventId(1L);
        return event;
    }

    private Subscription createValidSubscription() {
        return new Subscription(TEST_TENANT_ID, TEST_EVENT_TYPE, "test-service", "http://localhost:8080/api/events");
    }
}

