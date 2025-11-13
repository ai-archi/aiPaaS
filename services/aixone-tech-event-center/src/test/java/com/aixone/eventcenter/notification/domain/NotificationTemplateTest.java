package com.aixone.eventcenter.notification.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationTemplate 领域模型单元测试
 */
@DisplayName("NotificationTemplate 领域模型测试")
class NotificationTemplateTest {

    private NotificationTemplate template;
    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_TEMPLATE_NAME = "welcome-email";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;
    private final String TEST_SUBJECT = "欢迎 {{userName}}";
    private final String TEST_BODY = "您好 {{userName}}，欢迎使用我们的服务！";
    private final String TEST_CHANNELS = "EMAIL,SMS";

    @BeforeEach
    void setUp() {
        template = new NotificationTemplate(TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, 
                                           TEST_SUBJECT, TEST_BODY, TEST_CHANNELS);
    }

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建模板对象")
        void shouldCreateTemplateCorrectly() {
            // Then
            assertNotNull(template);
            assertEquals(TEST_TENANT_ID, template.getTenantId());
            assertEquals(TEST_TEMPLATE_NAME, template.getTemplateName());
            assertEquals(TEST_TYPE, template.getNotificationType());
            assertEquals(TEST_SUBJECT, template.getSubjectTemplate());
            assertEquals(TEST_BODY, template.getBodyTemplate());
            assertEquals(TEST_CHANNELS, template.getChannels());
            assertEquals(1, template.getVersion());
            assertNotNull(template.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("内容更新测试")
    class ContentUpdateTests {

        @Test
        @DisplayName("应该能够更新模板内容并增加版本号")
        void shouldUpdateContentAndIncrementVersion() {
            // Given
            String newSubject = "新的主题";
            String newBody = "新的内容";
            int originalVersion = template.getVersion();

            // When
            template.updateContent(newSubject, newBody);

            // Then
            assertEquals(newSubject, template.getSubjectTemplate());
            assertEquals(newBody, template.getBodyTemplate());
            assertEquals(originalVersion + 1, template.getVersion());
            assertNotNull(template.getUpdatedAt());
        }

        @Test
        @DisplayName("应该能够更新渠道配置")
        void shouldUpdateChannels() {
            // Given
            String newChannels = "EMAIL,PUSH";

            // When
            template.updateChannels(newChannels);

            // Then
            assertEquals(newChannels, template.getChannels());
            assertNotNull(template.getUpdatedAt());
        }

        @Test
        @DisplayName("应该能够更新变量定义")
        void shouldUpdateVariables() {
            // Given
            String variables = "{\"userName\":\"string\",\"userId\":\"number\"}";

            // When
            template.updateVariables(variables);

            // Then
            assertEquals(variables, template.getVariables());
            assertNotNull(template.getUpdatedAt());
        }
    }
}

