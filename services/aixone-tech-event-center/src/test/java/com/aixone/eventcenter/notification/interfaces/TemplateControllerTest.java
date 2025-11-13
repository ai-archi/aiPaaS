package com.aixone.eventcenter.notification.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.session.SessionContext;
import com.aixone.eventcenter.notification.application.TemplateApplicationService;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
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
 * TemplateController 控制器测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateController 控制器测试")
class TemplateControllerTest {

    @Mock
    private TemplateApplicationService templateApplicationService;

    private TemplateController templateController;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_TEMPLATE_NAME = "welcome-email";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;

    @BeforeEach
    void setUp() {
        templateController = new TemplateController();
        try {
            java.lang.reflect.Field serviceField = TemplateController.class.getDeclaredField("templateApplicationService");
            serviceField.setAccessible(true);
            serviceField.set(templateController, templateApplicationService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up TemplateController dependencies", e);
        }
    }

    @Nested
    @DisplayName("创建模板测试")
    class CreateTemplateTests {

        @Test
        @DisplayName("应该成功创建模板")
        void shouldCreateTemplateSuccessfully() {
            // Given
            TemplateController.CreateTemplateRequest request = new TemplateController.CreateTemplateRequest();
            request.setTemplateName(TEST_TEMPLATE_NAME);
            request.setNotificationType(TEST_TYPE);
            request.setSubjectTemplate("Subject");
            request.setBodyTemplate("Body");
            request.setChannels("EMAIL");

            NotificationTemplate savedTemplate = createValidTemplate();
            savedTemplate.setTemplateId(1L);

            when(templateApplicationService.createTemplate(
                    eq(TEST_TENANT_ID), eq(TEST_TEMPLATE_NAME), eq(TEST_TYPE), 
                    eq("Subject"), eq("Body"), eq("EMAIL"), isNull()))
                    .thenReturn(savedTemplate);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<NotificationTemplate> result = templateController.createTemplate(request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(savedTemplate, result.getData());
                verify(templateApplicationService).createTemplate(
                        TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, 
                        "Subject", "Body", "EMAIL", null);
            }
        }
    }

    @Nested
    @DisplayName("查询模板测试")
    class QueryTemplateTests {

        @Test
        @DisplayName("应该成功获取模板列表")
        void shouldGetTemplatesSuccessfully() {
            // Given
            List<NotificationTemplate> templates = Arrays.asList(createValidTemplate(), createValidTemplate());
            when(templateApplicationService.getTemplatesByTenant(TEST_TENANT_ID)).thenReturn(templates);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<List<NotificationTemplate>> result = templateController.getTemplates();

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(templateApplicationService).getTemplatesByTenant(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功根据ID获取模板")
        void shouldGetTemplateByIdSuccessfully() {
            // Given
            Long templateId = 1L;
            NotificationTemplate template = createValidTemplate();
            template.setTemplateId(templateId);

            when(templateApplicationService.getTemplateById(templateId, TEST_TENANT_ID))
                    .thenReturn(Optional.of(template));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<NotificationTemplate> result = templateController.getTemplateById(templateId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(template, result.getData());
                verify(templateApplicationService).getTemplateById(templateId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("模板不存在应该返回错误")
        void nonExistentTemplateShouldReturnError() {
            // Given
            Long templateId = 999L;
            when(templateApplicationService.getTemplateById(templateId, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<NotificationTemplate> result = templateController.getTemplateById(templateId);

                // Then
                assertNotNull(result);
                assertEquals(40401, result.getCode());
                assertEquals("模板不存在", result.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("更新模板测试")
    class UpdateTemplateTests {

        @Test
        @DisplayName("应该成功更新模板")
        void shouldUpdateTemplateSuccessfully() {
            // Given
            Long templateId = 1L;
            TemplateController.UpdateTemplateRequest request = new TemplateController.UpdateTemplateRequest();
            request.setSubjectTemplate("New Subject");
            request.setBodyTemplate("New Body");

            NotificationTemplate updatedTemplate = createValidTemplate();
            updatedTemplate.setTemplateId(templateId);

            when(templateApplicationService.updateTemplate(
                    eq(templateId), eq(TEST_TENANT_ID), eq("New Subject"), eq("New Body"), isNull(), isNull()))
                    .thenReturn(updatedTemplate);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);

                ApiResponse<NotificationTemplate> result = templateController.updateTemplate(templateId, request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(updatedTemplate, result.getData());
                verify(templateApplicationService).updateTemplate(
                        templateId, TEST_TENANT_ID, "New Subject", "New Body", null, null);
            }
        }
    }

    private NotificationTemplate createValidTemplate() {
        return new NotificationTemplate(TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, 
                                       "Subject", "Body", "EMAIL");
    }
}

