package com.aixone.eventcenter.schedule.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.schedule.domain.*;
import com.aixone.common.session.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    private TaskLogRepository taskLogRepository;

    @Mock
    private TaskSchedulerService taskSchedulerService;

    private TaskApplicationService taskApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_USER_ID = "user-001";

    @BeforeEach
    void setUp() {
        taskApplicationService = new TaskApplicationService();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field taskRepositoryField = TaskApplicationService.class.getDeclaredField("taskRepository");
            taskRepositoryField.setAccessible(true);
            taskRepositoryField.set(taskApplicationService, taskRepository);
            
            java.lang.reflect.Field taskLogRepositoryField = TaskApplicationService.class.getDeclaredField("taskLogRepository");
            taskLogRepositoryField.setAccessible(true);
            taskLogRepositoryField.set(taskApplicationService, taskLogRepository);
            
            java.lang.reflect.Field taskSchedulerServiceField = TaskApplicationService.class.getDeclaredField("taskSchedulerService");
            taskSchedulerServiceField.setAccessible(true);
            taskSchedulerServiceField.set(taskApplicationService, taskSchedulerService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up TaskApplicationService dependencies", e);
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
            Task savedTask = createValidTask();
            savedTask.setTaskId(1L);
            
            when(taskRepository.findByTaskNameAndTenantId(command.getTaskName(), TEST_TENANT_ID))
                .thenReturn(Optional.empty());
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
            doNothing().when(taskSchedulerService).scheduleTask(any(Task.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                mockedSessionContext.when(SessionContext::getUserId).thenReturn(TEST_USER_ID);
                
                Task result = taskApplicationService.createTask(command);

                // Then
                assertNotNull(result);
                assertEquals(savedTask, result);
                verify(taskRepository).findByTaskNameAndTenantId(command.getTaskName(), TEST_TENANT_ID);
                verify(taskRepository).save(any(Task.class));
                verify(taskSchedulerService).scheduleTask(any(Task.class));
            }
        }

        @Test
        @DisplayName("任务名称已存在应该抛出异常")
        void existingTaskNameShouldThrowException() {
            // Given
            CreateTaskCommand command = createValidCreateTaskCommand();
            Task existingTask = createValidTask();
            existingTask.setTaskId(1L);
            
            when(taskRepository.findByTaskNameAndTenantId(command.getTaskName(), TEST_TENANT_ID))
                .thenReturn(Optional.of(existingTask));

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, 
                    () -> taskApplicationService.createTask(command));
                assertEquals("TASK_NAME_EXISTS", exception.getErrorCode());
                assertEquals("任务名称已存在: " + command.getTaskName(), exception.getMessage());
            }
        }

        @Test
        @DisplayName("创建任务命令为空应该抛出异常")
        void nullCommandShouldThrowException() {
            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> taskApplicationService.createTask(null));
            }
        }

        @Test
        @DisplayName("任务名称为空应该抛出异常")
        void nullTaskNameShouldThrowException() {
            // Given
            CreateTaskCommand command = createValidCreateTaskCommand();
            command.setTaskName(null);

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> taskApplicationService.createTask(command));
            }
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
            Task task = createValidTask();
            task.setTaskId(taskId);
            Task savedTask = createValidTask();
            savedTask.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
            doNothing().when(taskSchedulerService).rescheduleTask(any(Task.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Task result = taskApplicationService.updateTask(taskId, command);

                // Then
                assertNotNull(result);
                assertEquals(savedTask, result);
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskRepository).save(any(Task.class));
                verify(taskSchedulerService).rescheduleTask(any(Task.class));
            }
        }

        @Test
        @DisplayName("更新不存在的任务应该抛出异常")
        void updateNonExistentTaskShouldThrowException() {
            // Given
            Long taskId = 999L;
            UpdateTaskCommand command = createValidUpdateTaskCommand();
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, 
                    () -> taskApplicationService.updateTask(taskId, command));
                assertEquals("TASK_NOT_FOUND", exception.getErrorCode());
                assertEquals("任务不存在: " + taskId, exception.getMessage());
            }
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
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            doNothing().when(taskSchedulerService).unscheduleTask(taskId);
            doNothing().when(taskLogRepository).deleteByTaskIdAndTenantId(taskId, TEST_TENANT_ID);
            doNothing().when(taskRepository).deleteById(taskId);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                taskApplicationService.deleteTask(taskId);

                // Then
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskSchedulerService).unscheduleTask(taskId);
                verify(taskLogRepository).deleteByTaskIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskRepository).deleteById(taskId);
            }
        }

        @Test
        @DisplayName("删除不存在的任务应该抛出异常")
        void deleteNonExistentTaskShouldThrowException() {
            // Given
            Long taskId = 999L;
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, 
                    () -> taskApplicationService.deleteTask(taskId));
                assertEquals("TASK_NOT_FOUND", exception.getErrorCode());
                assertEquals("任务不存在: " + taskId, exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("任务状态管理测试")
    class TaskStatusManagementTests {

        @Test
        @DisplayName("应该成功暂停任务")
        void shouldPauseTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            doNothing().when(taskSchedulerService).unscheduleTask(taskId);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                taskApplicationService.pauseTask(taskId);

                // Then
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskRepository).save(task);
                verify(taskSchedulerService).unscheduleTask(taskId);
            }
        }

        @Test
        @DisplayName("应该成功恢复任务")
        void shouldResumeTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            doNothing().when(taskSchedulerService).scheduleTask(any(Task.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                taskApplicationService.resumeTask(taskId);

                // Then
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskRepository).save(task);
                verify(taskSchedulerService).scheduleTask(task);
            }
        }

        @Test
        @DisplayName("应该成功取消任务")
        void shouldCancelTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(task);
            doNothing().when(taskSchedulerService).unscheduleTask(taskId);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                taskApplicationService.cancelTask(taskId);

                // Then
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskRepository).save(task);
                verify(taskSchedulerService).unscheduleTask(taskId);
            }
        }
    }

    @Nested
    @DisplayName("任务执行测试")
    class TaskExecutionTests {

        @Test
        @DisplayName("应该成功立即执行任务")
        void shouldExecuteTaskSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            task.setEnabled(true);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));
            doNothing().when(taskSchedulerService).executeTaskImmediately(any(Task.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                taskApplicationService.executeTask(taskId);

                // Then
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
                verify(taskSchedulerService).executeTaskImmediately(task);
            }
        }

        @Test
        @DisplayName("执行已禁用的任务应该抛出异常")
        void executeDisabledTaskShouldThrowException() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            task.setEnabled(false);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, 
                    () -> taskApplicationService.executeTask(taskId));
                assertEquals("TASK_DISABLED", exception.getErrorCode());
                assertEquals("任务已禁用，无法执行", exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("查询任务测试")
    class QueryTaskTests {

        @Test
        @DisplayName("应该成功根据ID查询任务")
        void shouldGetTaskByIdSuccessfully() {
            // Given
            Long taskId = 1L;
            Task task = createValidTask();
            task.setTaskId(taskId);
            
            when(taskRepository.findByIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(Optional.of(task));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Optional<Task> result = taskApplicationService.getTaskById(taskId);

                // Then
                assertTrue(result.isPresent());
                assertEquals(taskId, result.get().getId());
                verify(taskRepository).findByIdAndTenantId(taskId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功分页查询任务")
        void shouldGetTasksWithPagination() {
            // Given
            Pageable pageable = mock(Pageable.class);
            List<Task> tasks = Arrays.asList(createValidTask(), createValidTask());
            Page<Task> page = new PageImpl<>(tasks);
            
            when(taskRepository.findByTenantId(TEST_TENANT_ID, pageable)).thenReturn(page);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Page<Task> result = taskApplicationService.getTasks(pageable);

                // Then
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                verify(taskRepository).findByTenantId(TEST_TENANT_ID, pageable);
            }
        }

        @Test
        @DisplayName("应该成功根据状态查询任务")
        void shouldGetTasksByStatus() {
            // Given
            List<Task> tasks = Arrays.asList(createValidTask(), createValidTask());
            
            when(taskRepository.findByStatusAndTenantId(TaskStatus.PENDING, TEST_TENANT_ID))
                .thenReturn(tasks);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                List<Task> result = taskApplicationService.getTasksByStatus(TaskStatus.PENDING);

                // Then
                assertNotNull(result);
                assertEquals(2, result.size());
                verify(taskRepository).findByStatusAndTenantId(TaskStatus.PENDING, TEST_TENANT_ID);
            }
        }
    }

    @Nested
    @DisplayName("任务统计测试")
    class TaskStatisticsTests {

        @Test
        @DisplayName("应该成功获取任务统计信息")
        void shouldGetTaskStatisticsSuccessfully() {
            // Given
            Long taskId = 1L;
            long totalCount = 100L;
            long successCount = 80L;
            long failCount = 20L;
            
            when(taskLogRepository.countByTaskIdAndTenantId(taskId, TEST_TENANT_ID))
                .thenReturn(totalCount);
            when(taskLogRepository.countByTaskIdAndStatusAndTenantId(taskId, TaskStatus.SUCCESS, TEST_TENANT_ID))
                .thenReturn(successCount);
            when(taskLogRepository.countByTaskIdAndStatusAndTenantId(taskId, TaskStatus.FAILED, TEST_TENANT_ID))
                .thenReturn(failCount);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                TaskStatistics result = taskApplicationService.getTaskStatistics(taskId);

                // Then
                assertNotNull(result);
                assertEquals(taskId, result.getTaskId());
                assertEquals(totalCount, result.getTotalCount());
                assertEquals(successCount, result.getSuccessCount());
                assertEquals(failCount, result.getFailCount());
            }
        }
    }

    private CreateTaskCommand createValidCreateTaskCommand() {
        CreateTaskCommand command = new CreateTaskCommand();
        command.setTaskName("test-task");
        command.setDescription("Test task description");
        command.setTaskType(TaskType.CRON);
        command.setScheduleExpression("0 0 12 * * ?");
        command.setExecutorService("test-service");
        command.setTaskParams("{\"param1\":\"value1\"}");
        command.setMaxRetryCount(3);
        command.setTimeoutSeconds(300);
        return command;
    }

    private UpdateTaskCommand createValidUpdateTaskCommand() {
        UpdateTaskCommand command = new UpdateTaskCommand();
        command.setDescription("Updated description");
        command.setScheduleExpression("0 0 18 * * ?");
        command.setTaskParams("{\"param1\":\"updated_value\"}");
        command.setMaxRetryCount(5);
        command.setTimeoutSeconds(600);
        return command;
    }

    private Task createValidTask() {
        Task task = new Task();
        task.setTaskName("test-task");
        task.setDescription("Test task description");
        task.setTaskType(TaskType.CRON);
        task.setScheduleExpression("0 0 12 * * ?");
        task.setExecutorService("test-service");
        task.setTaskParams("{\"param1\":\"value1\"}");
        task.setMaxRetryCount(3);
        task.setTimeoutSeconds(300);
        task.setEnabled(true);
        task.setStatus(TaskStatus.PENDING);
        task.setCreator(TEST_USER_ID);
        task.setCreateTime(Instant.now());
        task.setUpdateTime(Instant.now());
        return task;
    }
}