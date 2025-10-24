package com.aixone.eventcenter.schedule.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务实体测试
 */
class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(
            "测试任务",
            "这是一个测试任务",
            TaskType.CRON,
            "0 0 12 * * ?",
            "test-service",
            "{\"param1\": \"value1\"}"
        );
    }

    @Test
    @DisplayName("应该成功创建任务")
    void shouldCreateTaskSuccessfully() {
        assertNotNull(task);
        assertNull(task.getTaskId()); // ID should be null before persistence
        assertEquals("测试任务", task.getTaskName());
        assertEquals("这是一个测试任务", task.getDescription());
        assertEquals(TaskType.CRON, task.getTaskType());
        assertEquals("0 0 12 * * ?", task.getScheduleExpression());
        assertEquals("test-service", task.getExecutorService());
        assertEquals("{\"param1\": \"value1\"}", task.getTaskParams());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertTrue(task.getEnabled());
        assertEquals(3, task.getMaxRetryCount());
        assertEquals(300, task.getTimeoutSeconds());
    }

    @Test
    @DisplayName("应该成功更新任务状态")
    void shouldUpdateTaskStatusSuccessfully() {
        task.updateStatus(TaskStatus.RUNNING);
        assertEquals(TaskStatus.RUNNING, task.getStatus());
        assertNotNull(task.getUpdateTime());
    }

    @Test
    @DisplayName("应该成功增加重试次数")
    void shouldIncrementRetryCountSuccessfully() {
        int initialRetryCount = task.getCurrentRetryCount();
        task.incrementRetryCount();
        assertEquals(initialRetryCount + 1, task.getCurrentRetryCount());
    }

    @Test
    @DisplayName("应该成功重置重试次数")
    void shouldResetRetryCountSuccessfully() {
        task.incrementRetryCount();
        task.incrementRetryCount();
        task.resetRetryCount();
        assertEquals(0, task.getCurrentRetryCount());
    }

    @Test
    @DisplayName("应该正确检查是否可以重试")
    void shouldCheckRetryCapabilityCorrectly() {
        assertTrue(task.canRetry());
        
        task.setCurrentRetryCount(3);
        assertFalse(task.canRetry());
    }

    @Test
    @DisplayName("应该成功暂停任务")
    void shouldPauseTaskSuccessfully() {
        task.pause();
        assertEquals(TaskStatus.PAUSED, task.getStatus());
        assertFalse(task.getEnabled());
    }

    @Test
    @DisplayName("应该成功恢复任务")
    void shouldResumeTaskSuccessfully() {
        task.pause();
        task.resume();
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertTrue(task.getEnabled());
    }

    @Test
    @DisplayName("应该成功取消任务")
    void shouldCancelTaskSuccessfully() {
        task.cancel();
        assertEquals(TaskStatus.CANCELLED, task.getStatus());
        assertFalse(task.getEnabled());
    }

    @Test
    @DisplayName("应该成功更新执行时间")
    void shouldUpdateExecuteTimeSuccessfully() {
        Instant now = Instant.now();
        Instant nextTime = now.plusSeconds(3600);
        
        task.updateExecuteTime(now, nextTime);
        assertEquals(now, task.getLastExecuteTime());
        assertEquals(nextTime, task.getNextExecuteTime());
    }
}
