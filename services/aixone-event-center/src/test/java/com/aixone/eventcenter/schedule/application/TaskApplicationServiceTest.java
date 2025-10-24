package com.aixone.eventcenter.schedule.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.schedule.domain.*;
import com.aixone.session.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TaskApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskApplicationService 应用服务测试")
class TaskApplicationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskSchedulerService taskSchedulerService;

    @InjectMocks
    private TaskApplicationService taskApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_USER_ID = "user-001";

    @BeforeEach
    void setUp() {
        // 模拟 SessionContext
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
            mockedSessionContext.when(SessionContext::getUserId).thenReturn(TEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("创建任务测试")
    class CreateTaskTests {

        @Test
        @DisplayName("应该成功创建任务")
        void shouldCreateTaskSuccessfully() {
            // Given
            CreateTaskCommand command = createValidCreateTaskCommand();
            Task expectedTask = createValidTask();
            
            when(taskRepository.findByTaskNameAndTenantId(anyString(), anyString()))
                .thenReturn(Optional.empty());
            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // When
            Task result = taskApplicationService.createTask(command);

            // Then
            assertNotNull(result);
            assertEquals(expectedTask, result);
            verify(taskRepository).findByTaskNameAndTenantId(command.getTaskName(), TEST_TENANT_ID);
            verify(taskRepository).save(any(Task.class));
            verify(taskSchedulerService).scheduleTask(any(Task.class));
        }

        @Test
        @DisplayName("任务名称重复应该抛出异常")
        void duplicateTaskNameShouldThrowException() {
            // Given
            CreateTaskCommand command = createValidCreateTaskCommand();
            Task existingTask = createValidTask();
            
            when(taskRepository.findByTaskNameAndTenantId(anyString(), anyString()))
                .thenReturn(Optional.of(existingTask));

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.createTask(command));
        }

        @Test
        @DisplayName("空命令应该抛出异常")
        void nullCommandShouldThrowException() {
            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.createTask(null));
        }

        @Test
        @DisplayName("空任务名称应该抛出异常")
        void nullTaskNameShouldThrowException() {
            // Given
            CreateTaskCommand command = createValidCreateTaskCommand();
            command.setTaskName(null);

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.createTask(command));
        }
    }

    @Nested
    @DisplayName("更新任务测试")
    class UpdateTaskTests {

        @Test
        @DisplayName("应该成功更新任务")
        void shouldUpdateTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            UpdateTaskCommand command = createValidUpdateTaskCommand();
            Task existingTask = createValidTask();
            existingTask.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

            // When
            Task result = taskApplicationService.updateTask(taskId, command);

            // Then
            assertNotNull(result);
            verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
            verify(taskRepository).save(any(Task.class));
            verify(taskSchedulerService).rescheduleTask(any(Task.class));
        }

        @Test
        @DisplayName("任务不存在应该抛出异常")
        void nonExistentTaskShouldThrowException() {
            // Given
            Long taskId = 1L;
            UpdateTaskCommand command = createValidUpdateTaskCommand();
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.updateTask(taskId, command));
        }
    }

    @Nested
    @DisplayName("启用任务测试")
    class EnableTaskTests {

        @Test
        @DisplayName("应该成功启用任务")
        void shouldEnableTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task disabledTask = createValidTask();
            disabledTask.setTaskId(taskId);
            disabledTask.setEnabled(false);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(disabledTask));
            when(taskRepository.save(any(Task.class))).thenReturn(disabledTask);

            // When
            Task result = taskApplicationService.enableTask(taskId);

            // Then
            assertNotNull(result);
            assertTrue(result.getEnabled());
            verify(taskSchedulerService).scheduleTask(any(Task.class));
        }

        @Test
        @DisplayName("已启用的任务应该抛出异常")
        void alreadyEnabledTaskShouldThrowException() {
            // Given
            Long taskId = 1L;
            Task enabledTask = createValidTask();
            enabledTask.setTaskId(taskId);
            enabledTask.setEnabled(true);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(enabledTask));

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.enableTask(taskId));
        }
    }

    @Nested
    @DisplayName("禁用任务测试")
    class DisableTaskTests {

        @Test
        @DisplayName("应该成功禁用任务")
        void shouldDisableTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task enabledTask = createValidTask();
            enabledTask.setTaskId(taskId);
            enabledTask.setEnabled(true);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(enabledTask));
            when(taskRepository.save(any(Task.class))).thenReturn(enabledTask);

            // When
            Task result = taskApplicationService.disableTask(taskId);

            // Then
            assertNotNull(result);
            assertFalse(result.getEnabled());
            verify(taskSchedulerService).unscheduleTask(taskId);
        }

        @Test
        @DisplayName("已禁用的任务应该抛出异常")
        void alreadyDisabledTaskShouldThrowException() {
            // Given
            Long taskId = 1L;
            Task disabledTask = createValidTask();
            disabledTask.setTaskId(taskId);
            disabledTask.setEnabled(false);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(disabledTask));

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.disableTask(taskId));
        }
    }

    @Nested
    @DisplayName("删除任务测试")
    class DeleteTaskTests {

        @Test
        @DisplayName("应该成功删除任务")
        void shouldDeleteTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            task.setEnabled(false);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));

            // When
            taskApplicationService.deleteTask(taskId);

            // Then
            verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
            verify(taskSchedulerService).unscheduleTask(taskId);
            verify(taskRepository).delete(task);
        }

        @Test
        @DisplayName("已启用的任务删除应该抛出异常")
        void enabledTaskDeletionShouldThrowException() {
            // Given
            Long taskId = 1L;
            Task enabledTask = createValidTask();
            enabledTask.setTaskId(taskId);
            enabledTask.setEnabled(true);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(enabledTask));

            // When & Then
            assertThrows(BizException.class, () -> 
                taskApplicationService.deleteTask(taskId));
        }
    }

    @Nested
    @DisplayName("查询任务测试")
    class QueryTaskTests {

        @Test
        @DisplayName("应该成功根据ID获取任务")
        void shouldGetTaskByIdSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));

            // When
            Optional<Task> result = taskApplicationService.getTaskById(taskId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(task, result.get());
        }

        @Test
        @DisplayName("应该成功获取所有任务")
        void shouldGetAllTasksSuccessfully() {
            // Given
            List<Task> tasks = Arrays.asList(createValidTask(), createValidTask());
            
            when(taskRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(tasks);

            // When
            List<Task> result = taskApplicationService.getAllTasks();

            // Then
            assertEquals(2, result.size());
            assertEquals(tasks, result);
        }

        @Test
        @DisplayName("应该成功获取启用的任务")
        void shouldGetEnabledTasksSuccessfully() {
            // Given
            List<Task> enabledTasks = Arrays.asList(createValidTask(), createValidTask());
            
            when(taskRepository.findByEnabledAndTenantId(true, TEST_TENANT_ID))
                .thenReturn(enabledTasks);

            // When
            List<Task> result = taskApplicationService.getEnabledTasks();

            // Then
            assertEquals(2, result.size());
            assertEquals(enabledTasks, result);
        }
    }

    // 辅助方法
    private CreateTaskCommand createValidCreateTaskCommand() {
        CreateTaskCommand command = new CreateTaskCommand();
        command.setTaskName("测试任务");
        command.setDescription("这是一个测试任务");
        command.setTaskType(TaskType.CRON_JOB);
        command.setScheduleStrategy(ScheduleStrategy.cron("0 0 12 * * ?"));
        command.setExecutorService("test-service");
        command.setTaskParams("{\"param1\": \"value1\"}");
        command.setMaxRetryCount(3);
        command.setTimeoutSeconds(300);
        return command;
    }

    private UpdateTaskCommand createValidUpdateTaskCommand() {
        UpdateTaskCommand command = new UpdateTaskCommand();
        command.setDescription("更新后的任务描述");
        command.setScheduleStrategy(ScheduleStrategy.cron("0 0 18 * * ?"));
        command.setExecutorService("updated-service");
        command.setTaskParams("{\"param2\": \"value2\"}");
        command.setMaxRetryCount(5);
        command.setTimeoutSeconds(600);
        return command;
    }

    private Task createValidTask() {
        Task task = new Task();
        task.setTaskId(1L);
        task.setTaskName("测试任务");
        task.setDescription("这是一个测试任务");
        task.setTaskType(TaskType.CRON_JOB);
        task.setScheduleExpression("0 0 12 * * ?");
        task.setExecutorService("test-service");
        task.setTaskParams("{\"param1\": \"value1\"}");
        task.setStatus(TaskStatus.PENDING);
        task.setEnabled(true);
        task.setMaxRetryCount(3);
        task.setTimeoutSeconds(300);
        task.setTenantId(TEST_TENANT_ID);
        return task;
    }
}
