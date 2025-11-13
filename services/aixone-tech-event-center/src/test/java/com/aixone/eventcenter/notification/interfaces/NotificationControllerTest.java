package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.session.SessionContext;
import com.aixone.eventcenter.notification.application.NotificationApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
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
 * NotificationController 控制器测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationController 控制器测试")
class NotificationControllerTest {

    @Mock
    private NotificationApplicationService notificationApplicationService;

    private NotificationController notificationController;

    private final String TEST_TENANT_ID = "tenant-001";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;
    private final String TEST_RECIPIENT = "{\"email\":\"test@example.com\"}";
    private final String TEST_CONTENT = "{\"subject\":\"Test\",\"body\":\"Test body\"}";
    private final Notification.NotificationChannel TEST_CHANNEL = Notification.NotificationChannel.EMAIL;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController();
        try {
            java.lang.reflect.Field serviceField = NotificationController.class.getDeclaredField("notificationApplicationService");
            serviceField.setAccessible(true);
            serviceField.set(notificationController, notificationApplicationService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up NotificationController dependencies", e);
        }
    }

    @Nested
    @DisplayName("发送通知测试")
    class SendNotificationTests {

        @Test
        @DisplayName("应该成功发送通知")
        void shouldSendNotificationSuccessfully() {
            // Given
            NotificationController.SendNotificationRequest request = new NotificationController.SendNotificationRequest();
            request.setNotificationType(TEST_TYPE);
            request.setRecipientInfo(TEST_RECIPIENT);
            request.setNotificationContent(TEST_CONTENT);
            request.setChannel(TEST_CHANNEL);

            Notification savedNotification = createValidNotification();
            savedNotification.setNotificationId(1L);

            when(notificationApplicationService.sendNotification(
                    eq(TEST_TENANT_ID), eq(TEST_TYPE), eq(TEST_RECIPIENT), eq(TEST_CONTENT), 
                    eq(TEST_CHANNEL), isNull(), isNull()))
                    .thenReturn(savedNotification);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Notification> result = notificationController.sendNotification(request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(savedNotification, result.getData());
                verify(notificationApplicationService).sendNotification(
                        TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, 
                        TEST_CHANNEL, null, null);
            }
        }

        @Test
        @DisplayName("缺少租户ID应该返回错误")
        void missingTenantIdShouldReturnError() {
            // Given
            NotificationController.SendNotificationRequest request = new NotificationController.SendNotificationRequest();

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);

                ApiResponse<Notification> result = notificationController.sendNotification(request);

                // Then
                assertNotNull(result);
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
                verify(notificationApplicationService, never()).sendNotification(any(), any(), any(), any(), any(), any(), any());
            }
        }
    }

    @Nested
    @DisplayName("查询通知测试")
    class QueryNotificationTests {

        @Test
        @DisplayName("应该成功获取通知列表")
        void shouldGetNotificationsSuccessfully() {
            // Given
            List<Notification> notifications = Arrays.asList(createValidNotification(), createValidNotification());
            when(notificationApplicationService.getNotificationsByTenant(TEST_TENANT_ID)).thenReturn(notifications);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<List<Notification>> result = notificationController.getNotifications(null, null, null);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(notificationApplicationService).getNotificationsByTenant(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功获取通知详情")
        void shouldGetNotificationByIdSuccessfully() {
            // Given
            Long notificationId = 1L;
            Notification notification = createValidNotification();
            notification.setNotificationId(notificationId);

            when(notificationApplicationService.getNotificationById(notificationId, TEST_TENANT_ID))
                    .thenReturn(Optional.of(notification));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Notification> result = notificationController.getNotificationById(notificationId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(notification, result.getData());
                verify(notificationApplicationService).getNotificationById(notificationId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("通知不存在应该返回错误")
        void nonExistentNotificationShouldReturnError() {
            // Given
            Long notificationId = 999L;
            when(notificationApplicationService.getNotificationById(notificationId, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<Notification> result = notificationController.getNotificationById(notificationId);

                // Then
                assertNotNull(result);
                assertEquals(40401, result.getCode());
                assertEquals("通知不存在", result.getMessage());
            }
        }
    }

    private Notification createValidNotification() {
        return new Notification(TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, TEST_CHANNEL);
    }
}

