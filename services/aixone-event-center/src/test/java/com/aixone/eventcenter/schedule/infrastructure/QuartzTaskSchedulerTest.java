package com.aixone.eventcenter.schedule.infrastructure;

import com.aixone.eventcenter.schedule.domain.Task;
import com.aixone.eventcenter.schedule.domain.TaskStatus;
import com.aixone.eventcenter.schedule.domain.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * QuartzTaskScheduler 基础设施测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuartzTaskScheduler 基础设施测试")
class QuartzTaskSchedulerTest {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private QuartzTaskScheduler quartzTaskScheduler;

    private Task testTask;

    @BeforeEach
    void setUp() throws SchedulerException {
        testTask = createValidTask();
    }

    @Nested
    @DisplayName("调度任务测试")
    class ScheduleJobTests {

        @Test
        @DisplayName("应该成功调度Cron任务")
        void shouldScheduleCronJobSuccessfully() throws SchedulerException {
            // Given
            testTask.setTaskType(TaskType.CRON);
            testTask.setScheduleExpression("0 0 12 * * ?");
            
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenReturn(Date.from(Instant.now()));

            // When
            quartzTaskScheduler.scheduleTask(testTask);

            // Then
            verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }

        @Test
        @DisplayName("应该成功调度一次性任务")
        void shouldScheduleOneTimeJobSuccessfully() throws SchedulerException {
            // Given
            testTask.setTaskType(TaskType.ONCE);
            testTask.setNextExecuteTime(Instant.now().plusSeconds(3600));
            
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenReturn(Date.from(Instant.now()));

            // When
            quartzTaskScheduler.scheduleTask(testTask);

            // Then
            verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }

        @Test
        @DisplayName("调度失败应该抛出异常")
        void scheduleFailureShouldThrowException() throws SchedulerException {
            // Given
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Scheduler error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.scheduleTask(testTask));
        }
    }

    @Nested
    @DisplayName("重新调度任务测试")
    class RescheduleJobTests {

        @Test
        @DisplayName("应该成功重新调度任务")
        void shouldRescheduleJobSuccessfully() throws SchedulerException {
            // Given
            when(scheduler.checkExists(any(TriggerKey.class))).thenReturn(true);
            when(scheduler.unscheduleJob(any(TriggerKey.class))).thenReturn(true);
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenReturn(Date.from(Instant.now()));

            // When
            quartzTaskScheduler.rescheduleTask(testTask);

            // Then
            verify(scheduler).checkExists(any(TriggerKey.class));
            verify(scheduler).unscheduleJob(any(TriggerKey.class));
            verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }

        @Test
        @DisplayName("重新调度失败应该抛出异常")
        void rescheduleFailureShouldThrowException() throws SchedulerException {
            // Given
            when(scheduler.checkExists(any(TriggerKey.class))).thenReturn(true);
            when(scheduler.unscheduleJob(any(TriggerKey.class))).thenReturn(true);
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Reschedule error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.rescheduleTask(testTask));
        }
    }

    @Nested
    @DisplayName("取消调度任务测试")
    class UnscheduleJobTests {

        @Test
        @DisplayName("应该成功取消调度任务")
        void shouldUnscheduleJobSuccessfully() throws SchedulerException {
            // Given
            Long taskId = 1L;
            when(scheduler.unscheduleJob(any(TriggerKey.class))).thenReturn(true);

            // When
            quartzTaskScheduler.unscheduleTask(taskId);

            // Then
            verify(scheduler).unscheduleJob(any(TriggerKey.class));
        }

        @Test
        @DisplayName("取消调度失败应该抛出异常")
        void unscheduleFailureShouldThrowException() throws SchedulerException {
            // Given
            Long taskId = 1L;
            when(scheduler.unscheduleJob(any(TriggerKey.class)))
                .thenThrow(new SchedulerException("Unschedule error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.unscheduleTask(taskId));
        }
    }

    @Nested
    @DisplayName("暂停任务测试")
    class PauseJobTests {

        @Test
        @DisplayName("应该成功暂停任务")
        void shouldPauseJobSuccessfully() throws SchedulerException {
            // Given
            Long taskId = 1L;
            doNothing().when(scheduler).pauseJob(any(JobKey.class));

            // When
            quartzTaskScheduler.pauseTask(taskId);

            // Then
            verify(scheduler).pauseJob(any(JobKey.class));
        }

        @Test
        @DisplayName("暂停失败应该抛出异常")
        void pauseFailureShouldThrowException() throws SchedulerException {
            // Given
            Long taskId = 1L;
            doThrow(new SchedulerException("Pause error"))
                .when(scheduler).pauseJob(any(JobKey.class));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.pauseTask(taskId));
        }
    }

    @Nested
    @DisplayName("恢复任务测试")
    class ResumeJobTests {

        @Test
        @DisplayName("应该成功恢复任务")
        void shouldResumeJobSuccessfully() throws SchedulerException {
            // Given
            Long taskId = 1L;
            doNothing().when(scheduler).resumeJob(any(JobKey.class));

            // When
            quartzTaskScheduler.resumeTask(taskId);

            // Then
            verify(scheduler).resumeJob(any(JobKey.class));
        }

        @Test
        @DisplayName("恢复失败应该抛出异常")
        void resumeFailureShouldThrowException() throws SchedulerException {
            // Given
            Long taskId = 1L;
            doThrow(new SchedulerException("Resume error"))
                .when(scheduler).resumeJob(any(JobKey.class));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.resumeTask(taskId));
        }
    }

    @Nested
    @DisplayName("立即执行测试")
    class ExecuteImmediatelyTests {

        @Test
        @DisplayName("应该成功立即执行任务")
        void shouldExecuteTaskImmediatelySuccessfully() throws SchedulerException {
            // Given
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenReturn(Date.from(Instant.now()));

            // When
            quartzTaskScheduler.executeTaskImmediately(testTask);

            // Then
            verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        }

        @Test
        @DisplayName("立即执行失败应该抛出异常")
        void executeImmediatelyFailureShouldThrowException() throws SchedulerException {
            // Given
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Execute immediately error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> 
                quartzTaskScheduler.executeTaskImmediately(testTask));
        }
    }

    @Nested
    @DisplayName("JobDetail构建测试")
    class JobDetailBuildingTests {

        @Test
        @DisplayName("应该正确构建JobDetail")
        void shouldBuildJobDetailCorrectly() throws SchedulerException {
            // Given
            testTask.setTaskId(1L);
            testTask.setTaskName("测试任务");
            testTask.setDescription("任务描述");
            testTask.setTaskType(TaskType.CRON);
            testTask.setExecutorService("test-service");
            testTask.setTaskParams("{\"param\": \"value\"}");
            testTask.setTimeoutSeconds(300);
            testTask.setMaxRetryCount(3);
            testTask.setCurrentRetryCount(0);
            testTask.setScheduleExpression("0 0 12 * * ?");
            // 通过反射设置租户ID
            try {
                java.lang.reflect.Field tenantIdField = testTask.getClass().getSuperclass().getDeclaredField("tenantId");
                tenantIdField.setAccessible(true);
                tenantIdField.set(testTask, "tenant-001");
            } catch (Exception e) {
                // 忽略反射异常
            }
            
            when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenReturn(Date.from(Instant.now()));

            // When
            quartzTaskScheduler.scheduleTask(testTask);

            // Then
            verify(scheduler).scheduleJob(argThat(jobDetail -> {
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                return jobDetail.getKey().getName().equals("1") &&
                       jobDetail.getKey().getGroup().equals("event-center-tasks") &&
                       jobDetail.getDescription().equals("任务描述") &&
                       jobDataMap.get("taskId").equals(1L) &&
                       jobDataMap.get("tenantId").equals("tenant-001") &&
                       jobDataMap.get("taskType").equals("CRON") &&
                       jobDataMap.get("taskName").equals("测试任务") &&
                       jobDataMap.get("executorService").equals("test-service") &&
                       jobDataMap.get("taskParams").equals("{\"param\": \"value\"}") &&
                       jobDataMap.get("timeoutSeconds").equals(300) &&
                       jobDataMap.get("maxRetryCount").equals(3) &&
                       jobDataMap.get("currentRetryCount").equals(0) &&
                       jobDataMap.get("scheduleExpression").equals("0 0 12 * * ?");
            }), any(Trigger.class));
        }
    }

    // 辅助方法
    private Task createValidTask() {
        Task task = new Task();
        task.setTaskId(1L);
        task.setTaskName("测试任务");
        task.setDescription("这是一个测试任务");
        task.setTaskType(TaskType.CRON);
        task.setScheduleExpression("0 0 12 * * ?");
        task.setExecutorService("test-service");
        task.setTaskParams("{\"param\": \"value\"}");
        task.setStatus(TaskStatus.PENDING);
        task.setEnabled(true);
        task.setMaxRetryCount(3);
        task.setCurrentRetryCount(0);
        task.setTimeoutSeconds(300);
        // 通过反射设置租户ID
        try {
            java.lang.reflect.Field tenantIdField = task.getClass().getSuperclass().getDeclaredField("tenantId");
            tenantIdField.setAccessible(true);
            tenantIdField.set(task, "tenant-001");
        } catch (Exception e) {
            // 忽略反射异常
        }
        return task;
    }
}
