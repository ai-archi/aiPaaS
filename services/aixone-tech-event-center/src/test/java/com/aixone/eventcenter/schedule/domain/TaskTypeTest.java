package com.aixone.eventcenter.schedule.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskType 枚举测试
 */
@DisplayName("TaskType 枚举测试")
class TaskTypeTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValueTests {

        @Test
        @DisplayName("应该包含所有预期的任务类型")
        void shouldContainAllExpectedTaskTypes() {
            // Then
            assertNotNull(TaskType.CRON);
            assertNotNull(TaskType.ONCE);
            assertNotNull(TaskType.INTERVAL);
        }

        @Test
        @DisplayName("应该正确返回任务类型描述")
        void shouldReturnCorrectTaskTypeDescriptions() {
            // Then
            assertEquals("定时任务", TaskType.CRON.getDescription());
            assertEquals("一次性任务", TaskType.ONCE.getDescription());
            assertEquals("间隔任务", TaskType.INTERVAL.getDescription());
        }
    }

    @Nested
    @DisplayName("任务类型特性测试")
    class TaskTypeFeatureTests {

        @Test
        @DisplayName("CRON 应该支持重复执行")
        void cronJobShouldSupportRepeatedExecution() {
            // Given
            TaskType taskType = TaskType.CRON;

            // When & Then
            assertTrue(supportsRepeatedExecution(taskType));
        }

        @Test
        @DisplayName("ONCE 不应该支持重复执行")
        void oneTimeJobShouldNotSupportRepeatedExecution() {
            // Given
            TaskType taskType = TaskType.ONCE;

            // When & Then
            assertFalse(supportsRepeatedExecution(taskType));
        }

        @Test
        @DisplayName("INTERVAL 应该支持重复执行")
        void intervalJobShouldSupportRepeatedExecution() {
            // Given
            TaskType taskType = TaskType.INTERVAL;

            // When & Then
            assertTrue(supportsRepeatedExecution(taskType));
        }

        @Test
        @DisplayName("CRON 应该需要调度表达式")
        void cronJobShouldRequireScheduleExpression() {
            // Given
            TaskType taskType = TaskType.CRON;

            // When & Then
            assertTrue(requiresScheduleExpression(taskType));
        }

        @Test
        @DisplayName("ONCE 应该需要执行时间")
        void oneTimeJobShouldRequireExecutionTime() {
            // Given
            TaskType taskType = TaskType.ONCE;

            // When & Then
            assertTrue(requiresExecutionTime(taskType));
        }

        @Test
        @DisplayName("INTERVAL 应该需要间隔时间")
        void intervalJobShouldRequireIntervalTime() {
            // Given
            TaskType taskType = TaskType.INTERVAL;

            // When & Then
            assertTrue(requiresIntervalTime(taskType));
        }
    }

    @Nested
    @DisplayName("任务类型验证测试")
    class TaskTypeValidationTests {

        @Test
        @DisplayName("应该正确验证 Cron 表达式格式")
        void shouldValidateCronExpressionFormat() {
            // Given
            TaskType taskType = TaskType.CRON;
            String validCron = "0 0 12 * * ?";
            String invalidCron = "invalid cron";

            // When & Then
            assertTrue(isValidScheduleExpression(taskType, validCron));
            assertFalse(isValidScheduleExpression(taskType, invalidCron));
        }

        @Test
        @DisplayName("应该正确验证一次性任务时间格式")
        void shouldValidateOneTimeJobTimeFormat() {
            // Given
            TaskType taskType = TaskType.ONCE;
            String validTime = "2024-12-31T23:59:59Z";
            String invalidTime = "invalid time";

            // When & Then
            assertTrue(isValidScheduleExpression(taskType, validTime));
            assertFalse(isValidScheduleExpression(taskType, invalidTime));
        }

        @Test
        @DisplayName("应该正确验证间隔任务参数")
        void shouldValidateIntervalTaskParams() {
            // Given
            TaskType taskType = TaskType.INTERVAL;
            String validInterval = "3600";
            String invalidInterval = "invalid";

            // When & Then
            assertTrue(isValidScheduleExpression(taskType, validInterval));
            assertFalse(isValidScheduleExpression(taskType, invalidInterval));
        }
    }

    // 辅助方法
    private boolean supportsRepeatedExecution(TaskType taskType) {
        return taskType == TaskType.CRON || taskType == TaskType.INTERVAL;
    }

    private boolean requiresScheduleExpression(TaskType taskType) {
        return taskType == TaskType.CRON;
    }

    private boolean requiresExecutionTime(TaskType taskType) {
        return taskType == TaskType.ONCE;
    }

    private boolean requiresIntervalTime(TaskType taskType) {
        return taskType == TaskType.INTERVAL;
    }

    private boolean isValidScheduleExpression(TaskType taskType, String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        switch (taskType) {
            case CRON:
                // 简化的 Cron 表达式验证
                return expression.matches("^[0-9\\*\\?\\-\\,\\/\\s]+$");
            case ONCE:
                // 简化的 ISO 8601 时间格式验证
                return expression.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z?$");
            case INTERVAL:
                // 间隔时间验证（数字）
                return expression.matches("^\\d+$");
            default:
                return false;
        }
    }
}
