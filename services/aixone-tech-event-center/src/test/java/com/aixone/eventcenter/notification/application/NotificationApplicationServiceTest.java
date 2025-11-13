package com.aixone.eventcenter.notification.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationRepository;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.eventcenter.notification.domain.NotificationTemplateRepository;
import com.aixone.eventcenter.notification.infrastructure.CompositeNotificationSender;
import com.aixone.eventcenter.notification.infrastructure.TemplateEngine;
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
 * NotificationApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationApplicationService 应用服务测试")
class NotificationApplicationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTemplateRepository templateRepository;

    @Mock
    private CompositeNotificationSender notificationSender;

    @Mock
    private TemplateEngine templateEngine;

    private NotificationApplicationService notificationApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;
    private final String TEST_RECIPIENT = "{\"email\":\"test@example.com\"}";
    private final String TEST_CONTENT = "{\"subject\":\"Test\",\"body\":\"Test body\"}";
    private final Notification.NotificationChannel TEST_CHANNEL = Notification.NotificationChannel.EMAIL;

    @BeforeEach
    void setUp() {
        notificationApplicationService = new NotificationApplicationService();
        try {
            java.lang.reflect.Field repositoryField = NotificationApplicationService.class.getDeclaredField("notificationRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(notificationApplicationService, notificationRepository);
            
            java.lang.reflect.Field templateRepositoryField = NotificationApplicationService.class.getDeclaredField("templateRepository");
            templateRepositoryField.setAccessible(true);
            templateRepositoryField.set(notificationApplicationService, templateRepository);
            
            java.lang.reflect.Field senderField = NotificationApplicationService.class.getDeclaredField("notificationSender");
            senderField.setAccessible(true);
            senderField.set(notificationApplicationService, notificationSender);
            
            java.lang.reflect.Field engineField = NotificationApplicationService.class.getDeclaredField("templateEngine");
            engineField.setAccessible(true);
            engineField.set(notificationApplicationService, templateEngine);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up NotificationApplicationService dependencies", e);
        }
    }

    @Nested
    @DisplayName("发送通知测试")
    class SendNotificationTests {

        @Test
        @DisplayName("应该成功发送通知")
        void shouldSendNotificationSuccessfully() {
            // Given
            Notification savedNotification = createValidNotification();
            savedNotification.setNotificationId(1L);
            
            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
            when(notificationSender.send(any(Notification.class))).thenReturn(true);

            // When
            Notification result = notificationApplicationService.sendNotification(
                    TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, TEST_CHANNEL, null, null);

            // Then
            assertNotNull(result);
            assertEquals(Notification.NotificationStatus.SENT, result.getStatus());
            verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
            verify(notificationSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("发送失败应该标记为失败状态")
        void shouldMarkAsFailedWhenSendFails() {
            // Given
            Notification savedNotification = createValidNotification();
            savedNotification.setNotificationId(1L);
            
            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
            when(notificationSender.send(any(Notification.class))).thenReturn(false);

            // When
            Notification result = notificationApplicationService.sendNotification(
                    TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, TEST_CHANNEL, null, null);

            // Then
            assertEquals(Notification.NotificationStatus.FAILED, result.getStatus());
            verify(notificationSender).send(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("使用模板发送通知测试")
    class SendNotificationWithTemplateTests {

        @Test
        @DisplayName("应该成功使用模板发送通知")
        void shouldSendNotificationWithTemplateSuccessfully() {
            // Given
            NotificationTemplate template = createValidTemplate();
            template.setTemplateId(1L);
            Notification savedNotification = createValidNotification();
            savedNotification.setNotificationId(1L);
            
            when(templateRepository.findByTemplateIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(template));
            when(templateEngine.render(anyString(), anyString())).thenReturn("渲染后的内容");
            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
            when(notificationSender.send(any(Notification.class))).thenReturn(true);

            // When
            Notification result = notificationApplicationService.sendNotificationWithTemplate(
                    TEST_TENANT_ID, 1L, TEST_RECIPIENT, "{\"userName\":\"John\"}");

            // Then
            assertNotNull(result);
            verify(templateRepository).findByTemplateIdAndTenantId(1L, TEST_TENANT_ID);
            verify(templateEngine, atLeastOnce()).render(anyString(), anyString());
            verify(notificationSender).send(any(Notification.class));
        }

        @Test
        @DisplayName("模板不存在应该抛出异常")
        void shouldThrowExceptionWhenTemplateNotFound() {
            // Given
            when(templateRepository.findByTemplateIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            BizException exception = assertThrows(BizException.class, () -> {
                notificationApplicationService.sendNotificationWithTemplate(
                        TEST_TENANT_ID, 1L, TEST_RECIPIENT, "{}");
            });
            assertEquals("TEMPLATE_NOT_FOUND", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("查询测试")
    class QueryTests {

        @Test
        @DisplayName("应该成功查询租户的所有通知")
        void shouldGetNotificationsByTenantSuccessfully() {
            // Given
            List<Notification> notifications = Arrays.asList(createValidNotification(), createValidNotification());
            when(notificationRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(notifications);

            // When
            List<Notification> result = notificationApplicationService.getNotificationsByTenant(TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(notificationRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功根据ID查询通知")
        void shouldGetNotificationByIdSuccessfully() {
            // Given
            Notification notification = createValidNotification();
            notification.setNotificationId(1L);
            when(notificationRepository.findByNotificationIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(notification));

            // When
            Optional<Notification> result = notificationApplicationService.getNotificationById(1L, TEST_TENANT_ID);

            // Then
            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getNotificationId());
            verify(notificationRepository).findByNotificationIdAndTenantId(1L, TEST_TENANT_ID);
        }
    }

    private Notification createValidNotification() {
        return new Notification(TEST_TENANT_ID, TEST_TYPE, TEST_RECIPIENT, TEST_CONTENT, TEST_CHANNEL);
    }

    private NotificationTemplate createValidTemplate() {
        return new NotificationTemplate(TEST_TENANT_ID, "test-template", TEST_TYPE, 
                                       "Subject", "Body", "EMAIL");
    }
}

