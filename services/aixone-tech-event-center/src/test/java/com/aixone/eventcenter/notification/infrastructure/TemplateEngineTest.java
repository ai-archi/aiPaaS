package com.aixone.eventcenter.notification.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TemplateEngine 基础设施测试
 */
@DisplayName("TemplateEngine 基础设施测试")
class TemplateEngineTest {

    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = new TemplateEngine();
    }

    @Nested
    @DisplayName("模板渲染测试")
    class RenderTests {

        @Test
        @DisplayName("应该成功渲染简单模板")
        void shouldRenderSimpleTemplate() {
            // Given
            String template = "Hello {{name}}";
            String variables = "{\"name\":\"World\"}";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("应该成功渲染多个变量的模板")
        void shouldRenderTemplateWithMultipleVariables() {
            // Given
            String template = "Hello {{firstName}} {{lastName}}";
            String variables = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertEquals("Hello John Doe", result);
        }

        @Test
        @DisplayName("变量不存在应该保留原样")
        void shouldKeepOriginalWhenVariableNotFound() {
            // Given
            String template = "Hello {{name}}";
            String variables = "{\"other\":\"value\"}";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertTrue(result.contains("{{name}}"));
        }

        @Test
        @DisplayName("空模板应该返回空字符串")
        void shouldReturnEmptyStringForEmptyTemplate() {
            // Given
            String template = "";
            String variables = "{\"name\":\"World\"}";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertEquals("", result);
        }

        @Test
        @DisplayName("空变量应该返回原模板")
        void shouldReturnOriginalTemplateForEmptyVariables() {
            // Given
            String template = "Hello {{name}}";
            String variables = "";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertEquals(template, result);
        }

        @Test
        @DisplayName("无效JSON应该返回原模板")
        void shouldReturnOriginalTemplateForInvalidJson() {
            // Given
            String template = "Hello {{name}}";
            String variables = "invalid json";

            // When
            String result = templateEngine.render(template, variables);

            // Then
            assertEquals(template, result);
        }
    }
}

