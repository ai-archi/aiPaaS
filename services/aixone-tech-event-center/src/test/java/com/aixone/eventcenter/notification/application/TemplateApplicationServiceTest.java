package com.aixone.eventcenter.notification.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.notification.domain.Notification;
import com.aixone.eventcenter.notification.domain.NotificationTemplate;
import com.aixone.eventcenter.notification.domain.NotificationTemplateRepository;
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
 * TemplateApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateApplicationService 应用服务测试")
class TemplateApplicationServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;

    private TemplateApplicationService templateApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_TEMPLATE_NAME = "welcome-email";
    private final Notification.NotificationType TEST_TYPE = Notification.NotificationType.EMAIL;

    @BeforeEach
    void setUp() {
        templateApplicationService = new TemplateApplicationService();
        try {
            java.lang.reflect.Field repositoryField = TemplateApplicationService.class.getDeclaredField("templateRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(templateApplicationService, templateRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up TemplateApplicationService dependencies", e);
        }
    }

    @Nested
    @DisplayName("创建模板测试")
    class CreateTemplateTests {

        @Test
        @DisplayName("应该成功创建模板")
        void shouldCreateTemplateSuccessfully() {
            // Given
            NotificationTemplate savedTemplate = createValidTemplate();
            savedTemplate.setTemplateId(1L);
            
            when(templateRepository.findByTenantIdAndTemplateName(TEST_TENANT_ID, TEST_TEMPLATE_NAME))
                    .thenReturn(Optional.empty());
            when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(savedTemplate);

            // When
            NotificationTemplate result = templateApplicationService.createTemplate(
                    TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, "Subject", "Body", "EMAIL", null);

            // Then
            assertNotNull(result);
            assertEquals(savedTemplate, result);
            verify(templateRepository).save(any(NotificationTemplate.class));
        }

        @Test
        @DisplayName("模板名称已存在应该抛出异常")
        void shouldThrowExceptionWhenTemplateNameExists() {
            // Given
            NotificationTemplate existing = createValidTemplate();
            when(templateRepository.findByTenantIdAndTemplateName(TEST_TENANT_ID, TEST_TEMPLATE_NAME))
                    .thenReturn(Optional.of(existing));

            // When & Then
            BizException exception = assertThrows(BizException.class, () -> {
                templateApplicationService.createTemplate(
                        TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, "Subject", "Body", "EMAIL", null);
            });
            assertEquals("TEMPLATE_NAME_EXISTS", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("更新模板测试")
    class UpdateTemplateTests {

        @Test
        @DisplayName("应该成功更新模板")
        void shouldUpdateTemplateSuccessfully() {
            // Given
            NotificationTemplate template = createValidTemplate();
            template.setTemplateId(1L);
            
            when(templateRepository.findByTemplateIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.of(template));
            when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(template);

            // When
            NotificationTemplate result = templateApplicationService.updateTemplate(
                    1L, TEST_TENANT_ID, "New Subject", "New Body", null, null);

            // Then
            assertNotNull(result);
            verify(templateRepository).save(any(NotificationTemplate.class));
        }

        @Test
        @DisplayName("模板不存在应该抛出异常")
        void shouldThrowExceptionWhenTemplateNotFound() {
            // Given
            when(templateRepository.findByTemplateIdAndTenantId(1L, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            BizException exception = assertThrows(BizException.class, () -> {
                templateApplicationService.updateTemplate(1L, TEST_TENANT_ID, null, null, null, null);
            });
            assertEquals("TEMPLATE_NOT_FOUND", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("查询测试")
    class QueryTests {

        @Test
        @DisplayName("应该成功查询租户的所有模板")
        void shouldGetTemplatesByTenantSuccessfully() {
            // Given
            List<NotificationTemplate> templates = Arrays.asList(createValidTemplate(), createValidTemplate());
            when(templateRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(templates);

            // When
            List<NotificationTemplate> result = templateApplicationService.getTemplatesByTenant(TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(templateRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功根据模板名称查询模板")
        void shouldGetTemplateByNameSuccessfully() {
            // Given
            NotificationTemplate template = createValidTemplate();
            when(templateRepository.findByTenantIdAndTemplateName(TEST_TENANT_ID, TEST_TEMPLATE_NAME))
                    .thenReturn(Optional.of(template));

            // When
            Optional<NotificationTemplate> result = templateApplicationService.getTemplateByName(TEST_TEMPLATE_NAME, TEST_TENANT_ID);

            // Then
            assertTrue(result.isPresent());
            assertEquals(TEST_TEMPLATE_NAME, result.get().getTemplateName());
            verify(templateRepository).findByTenantIdAndTemplateName(TEST_TENANT_ID, TEST_TEMPLATE_NAME);
        }
    }

    private NotificationTemplate createValidTemplate() {
        return new NotificationTemplate(TEST_TENANT_ID, TEST_TEMPLATE_NAME, TEST_TYPE, 
                                       "Subject", "Body", "EMAIL");
    }
}

