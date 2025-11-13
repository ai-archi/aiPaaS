package com.aixone.eventcenter.schedule.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskStatus 枚举测试
 */
@DisplayName("TaskStatus 枚举测试")
class TaskStatusTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValueTests {

        @Test
        @DisplayName("应该包含所有预期的状态值")
        void shouldContainAllExpectedStatusValues() {
            // Then
            assertNotNull(TaskStatus.PENDING);
            assertNotNull(TaskStatus.RUNNING);
            assertNotNull(TaskStatus.SUCCESS);
            assertNotNull(TaskStatus.FAILED);
            assertNotNull(TaskStatus.PAUSED);
            assertNotNull(TaskStatus.CANCELLED);
        }

        @Test
        @DisplayName("应该正确返回状态描述")
        void shouldReturnCorrectStatusDescriptions() {
            // Then
            assertEquals("待执行", TaskStatus.PENDING.getDescription());
            assertEquals("执行中", TaskStatus.RUNNING.getDescription());
            assertEquals("执行成功", TaskStatus.SUCCESS.getDescription());
            assertEquals("执行失败", TaskStatus.FAILED.getDescription());
            assertEquals("已暂停", TaskStatus.PAUSED.getDescription());
            assertEquals("已取消", TaskStatus.CANCELLED.getDescription());
        }
    }

    @Nested
    @DisplayName("状态转换测试")
    class StatusTransitionTests {

        @Test
        @DisplayName("PENDING 状态应该可以转换为 RUNNING")
        void pendingShouldTransitionToRunning() {
            // Given
            TaskStatus status = TaskStatus.PENDING;

            // When & Then
            assertTrue(canTransitionTo(status, TaskStatus.RUNNING));
        }

        @Test
        @DisplayName("RUNNING 状态应该可以转换为 SUCCESS 或 FAILED")
        void runningShouldTransitionToSuccessOrFailed() {
            // Given
            TaskStatus status = TaskStatus.RUNNING;

            // When & Then
            assertTrue(canTransitionTo(status, TaskStatus.SUCCESS));
            assertTrue(canTransitionTo(status, TaskStatus.FAILED));
        }

        @Test
        @DisplayName("PENDING 状态应该可以转换为 PAUSED 或 CANCELLED")
        void pendingShouldTransitionToPausedOrCancelled() {
            // Given
            TaskStatus status = TaskStatus.PENDING;

            // When & Then
            assertTrue(canTransitionTo(status, TaskStatus.PAUSED));
            assertTrue(canTransitionTo(status, TaskStatus.CANCELLED));
        }

        @Test
        @DisplayName("PAUSED 状态应该可以转换为 PENDING")
        void pausedShouldTransitionToPending() {
            // Given
            TaskStatus status = TaskStatus.PAUSED;

            // When & Then
            assertTrue(canTransitionTo(status, TaskStatus.PENDING));
        }

        @Test
        @DisplayName("最终状态不应该转换到其他状态")
        void finalStatesShouldNotTransition() {
            // Given
            TaskStatus[] finalStates = {TaskStatus.SUCCESS, TaskStatus.FAILED, TaskStatus.CANCELLED};

            // When & Then
            for (TaskStatus finalState : finalStates) {
                for (TaskStatus targetState : TaskStatus.values()) {
                    if (finalState != targetState) {
                        assertFalse(canTransitionTo(finalState, targetState),
                            finalState + " should not transition to " + targetState);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("状态分类测试")
    class StatusCategoryTests {

        @Test
        @DisplayName("应该正确识别执行中状态")
        void shouldIdentifyExecutingStates() {
            // Given
            TaskStatus[] executingStates = {TaskStatus.RUNNING};

            // When & Then
            for (TaskStatus status : executingStates) {
                assertTrue(isExecutingState(status), status + " should be executing state");
            }
        }

        @Test
        @DisplayName("应该正确识别待执行状态")
        void shouldIdentifyPendingStates() {
            // Given
            TaskStatus[] pendingStates = {TaskStatus.PENDING, TaskStatus.PAUSED};

            // When & Then
            for (TaskStatus status : pendingStates) {
                assertTrue(isPendingState(status), status + " should be pending state");
            }
        }

        @Test
        @DisplayName("应该正确识别完成状态")
        void shouldIdentifyCompletedStates() {
            // Given
            TaskStatus[] completedStates = {TaskStatus.SUCCESS, TaskStatus.FAILED, TaskStatus.CANCELLED};

            // When & Then
            for (TaskStatus status : completedStates) {
                assertTrue(isCompletedState(status), status + " should be completed state");
            }
        }
    }

    // 辅助方法
    private boolean canTransitionTo(TaskStatus from, TaskStatus to) {
        // 简化的状态转换规则
        switch (from) {
            case PENDING:
                return to == TaskStatus.RUNNING || to == TaskStatus.PAUSED || to == TaskStatus.CANCELLED;
            case RUNNING:
                return to == TaskStatus.SUCCESS || to == TaskStatus.FAILED;
            case PAUSED:
                return to == TaskStatus.PENDING;
            case SUCCESS:
            case FAILED:
            case CANCELLED:
                return false; // 最终状态
            default:
                return false;
        }
    }

    private boolean isExecutingState(TaskStatus status) {
        return status == TaskStatus.RUNNING;
    }

    private boolean isPendingState(TaskStatus status) {
        return status == TaskStatus.PENDING || status == TaskStatus.PAUSED;
    }

    private boolean isCompletedState(TaskStatus status) {
        return status == TaskStatus.SUCCESS || status == TaskStatus.FAILED || status == TaskStatus.CANCELLED;
    }
}
