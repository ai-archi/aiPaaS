package com.aixone.eventcenter.notification.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Notification 领域模型单元测试
 */
@DisplayName("Notification 领域模型测试")
class NotificationTest {

    private Notification notification;
    private final String TEST_TENANT_ID = "tenant-001";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;
    private final String TEST_RECIPIENT = "{\"email\":\"test@example.com\"}";
    private final String TEST_CONTENT = "{\"subject\":\"Test\",\"body\":\"Test body\"}";
    private final Notification.NotificationChannel TEST_CHANNEL = Notification.NotificationChannel.EMAIL;

    @BeforeEach
    void setUp() {
        notification = new Notification(TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, TEST_CHANNEL);
    }

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建通知对象")
        void shouldCreateNotificationCorrectly() {
            // Then
            assertNotNull(notification);
            assertEquals(TEST_TENANT_ID, notification.getTenantId());
            assertEquals(TEST_TYPE, notification.getNotificationType());
            assertEquals(TEST_RECIPIENT, notification.getRecipientInfo());
            assertEquals(TEST_CONTENT, notification.getNotificationContent());
            assertEquals(TEST_CHANNEL, notification.getChannel());
            assertEquals(Notification.NotificationStatus.PENDING, notification.getStatus());
            assertEquals(Notification.NotificationPriority.NORMAL, notification.getPriority());
            assertNotNull(notification.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("应该能够标记为已发送")
        void shouldMarkAsSent() {
            // When
            notification.markAsSent();

            // Then
            assertEquals(Notification.NotificationStatus.SENT, notification.getStatus());
            assertNotNull(notification.getSentAt());
        }

        @Test
        @DisplayName("应该能够标记为失败")
        void shouldMarkAsFailed() {
            // Given
            String errorMessage = "发送失败";

            // When
            notification.markAsFailed(errorMessage);

            // Then
            assertEquals(Notification.NotificationStatus.FAILED, notification.getStatus());
            assertEquals(errorMessage, notification.getErrorMessage());
        }

        @Test
        @DisplayName("应该能够取消通知")
        void shouldCancelNotification() {
            // When
            notification.cancel();

            // Then
            assertEquals(Notification.NotificationStatus.CANCELLED, notification.getStatus());
        }
    }

    @Nested
    @DisplayName("优先级测试")
    class PriorityTests {

        @Test
        @DisplayName("应该能够设置优先级")
        void shouldSetPriority() {
            // When
            notification.setPriority(Notification.NotificationPriority.HIGH);

            // Then
            assertEquals(Notification.NotificationPriority.HIGH, notification.getPriority());
        }
    }
}

