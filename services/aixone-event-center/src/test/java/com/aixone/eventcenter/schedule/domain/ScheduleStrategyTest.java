package com.aixone.eventcenter.schedule.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScheduleStrategy 值对象测试
 */
@DisplayName("ScheduleStrategy 值对象测试")
class ScheduleStrategyTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建 Cron 调度策略")
        void shouldCreateCronScheduleStrategy() {
            // Given
            String cronExpression = "0 0 12 * * ?";
            TaskType taskType = TaskType.CRON;

            // When
            ScheduleStrategy strategy = new ScheduleStrategy(cronExpression, taskType);

            // Then
            assertNotNull(strategy);
            assertEquals(cronExpression, strategy.getExpression());
            assertEquals(taskType, strategy.getTaskType());
            assertTrue(strategy.isCron());
            assertFalse(strategy.isOnce());
            assertFalse(strategy.isInterval());
        }

        @Test
        @DisplayName("应该正确创建一次性任务调度策略")
        void shouldCreateOnceScheduleStrategy() {
            // Given
            Instant executeTime = Instant.now().plusSeconds(3600);
            TaskType taskType = TaskType.ONCE;

            // When
            ScheduleStrategy strategy = new ScheduleStrategy(executeTime.toString(), taskType);

            // Then
            assertNotNull(strategy);
            assertEquals(executeTime.toString(), strategy.getExpression());
            assertEquals(taskType, strategy.getTaskType());
            assertFalse(strategy.isCron());
            assertTrue(strategy.isOnce());
            assertFalse(strategy.isInterval());
        }

        @Test
        @DisplayName("应该正确创建间隔任务调度策略")
        void shouldCreateIntervalScheduleStrategy() {
            // Given
            String intervalExpression = "3600"; // 1小时
            TaskType taskType = TaskType.INTERVAL;

            // When
            ScheduleStrategy strategy = new ScheduleStrategy(intervalExpression, taskType);

            // Then
            assertNotNull(strategy);
            assertEquals(intervalExpression, strategy.getExpression());
            assertEquals(taskType, strategy.getTaskType());
            assertFalse(strategy.isCron());
            assertFalse(strategy.isOnce());
            assertTrue(strategy.isInterval());
        }

        @Test
        @DisplayName("空表达式应该抛出异常")
        void nullExpressionShouldThrowException() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> 
                new ScheduleStrategy(null, TaskType.CRON));
        }

        @Test
        @DisplayName("空任务类型应该抛出异常")
        void nullTaskTypeShouldThrowException() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> 
                new ScheduleStrategy("0 0 12 * * ?", null));
        }

        @Test
        @DisplayName("空白表达式应该抛出异常")
        void blankExpressionShouldThrowException() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> 
                new ScheduleStrategy("   ", TaskType.CRON));
        }
    }

    @Nested
    @DisplayName("静态工厂方法测试")
    class StaticFactoryMethodTests {

        @Test
        @DisplayName("cron 方法应该创建正确的调度策略")
        void cronMethodShouldCreateCorrectStrategy() {
            // Given
            String cronExpression = "0 0 12 * * ?";

            // When
            ScheduleStrategy strategy = ScheduleStrategy.cron(cronExpression);

            // Then
            assertNotNull(strategy);
            assertEquals(cronExpression, strategy.getExpression());
            assertEquals(TaskType.CRON, strategy.getTaskType());
            assertTrue(strategy.isCron());
        }

        @Test
        @DisplayName("once 方法应该创建正确的调度策略")
        void onceMethodShouldCreateCorrectStrategy() {
            // Given
            Instant executeTime = Instant.now().plusSeconds(3600);

            // When
            ScheduleStrategy strategy = ScheduleStrategy.once(executeTime);

            // Then
            assertNotNull(strategy);
            assertEquals(executeTime.toString(), strategy.getExpression());
            assertEquals(TaskType.ONCE, strategy.getTaskType());
            assertTrue(strategy.isOnce());
        }

        @Test
        @DisplayName("interval 方法应该创建正确的调度策略")
        void intervalMethodShouldCreateCorrectStrategy() {
            // Given
            long intervalSeconds = 3600;

            // When
            ScheduleStrategy strategy = ScheduleStrategy.interval(intervalSeconds);

            // Then
            assertNotNull(strategy);
            assertEquals(String.valueOf(intervalSeconds), strategy.getExpression());
            assertEquals(TaskType.INTERVAL, strategy.getTaskType());
            assertTrue(strategy.isInterval());
        }

        @Test
        @DisplayName("interval 方法（带单位）应该创建正确的调度策略")
        void intervalMethodWithUnitShouldCreateCorrectStrategy() {
            // Given
            long interval = 1;
            ChronoUnit unit = ChronoUnit.HOURS;

            // When
            ScheduleStrategy strategy = ScheduleStrategy.interval(interval, unit);

            // Then
            assertNotNull(strategy);
            assertEquals("3600", strategy.getExpression()); // 1小时 = 3600秒
            assertEquals(TaskType.INTERVAL, strategy.getTaskType());
            assertTrue(strategy.isInterval());
        }
    }

    @Nested
    @DisplayName("业务方法测试")
    class BusinessMethodTests {

        private ScheduleStrategy cronStrategy;
        private ScheduleStrategy onceStrategy;
        private ScheduleStrategy intervalStrategy;

        @BeforeEach
        void setUp() {
            cronStrategy = ScheduleStrategy.cron("0 0 12 * * ?");
            onceStrategy = ScheduleStrategy.once(Instant.now().plusSeconds(3600));
            intervalStrategy = ScheduleStrategy.interval(3600);
        }

        @Test
        @DisplayName("应该正确获取 Cron 表达式")
        void shouldGetCronExpressionCorrectly() {
            // When
            String cronExpression = cronStrategy.getCronExpression();

            // Then
            assertEquals("0 0 12 * * ?", cronExpression);
        }

        @Test
        @DisplayName("非 Cron 任务获取 Cron 表达式应该抛出异常")
        void nonCronTaskGettingCronExpressionShouldThrowException() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                onceStrategy.getCronExpression());
        }

        @Test
        @DisplayName("应该正确获取执行时间")
        void shouldGetExecuteTimeCorrectly() {
            // Given
            Instant expectedTime = Instant.now().plusSeconds(3600);
            ScheduleStrategy strategy = ScheduleStrategy.once(expectedTime);

            // When
            Instant executeTime = strategy.getExecuteTime();

            // Then
            assertEquals(expectedTime, executeTime);
        }

        @Test
        @DisplayName("非一次性任务获取执行时间应该抛出异常")
        void nonOnceTaskGettingExecuteTimeShouldThrowException() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                cronStrategy.getExecuteTime());
        }

        @Test
        @DisplayName("应该正确获取间隔秒数")
        void shouldGetIntervalSecondsCorrectly() {
            // When
            long intervalSeconds = intervalStrategy.getIntervalSeconds();

            // Then
            assertEquals(3600, intervalSeconds);
        }

        @Test
        @DisplayName("非间隔任务获取间隔秒数应该抛出异常")
        void nonIntervalTaskGettingIntervalSecondsShouldThrowException() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                cronStrategy.getIntervalSeconds());
        }

        @Test
        @DisplayName("无效的间隔表达式应该抛出异常")
        void invalidIntervalExpressionShouldThrowException() {
            // Given
            ScheduleStrategy strategy = new ScheduleStrategy("invalid", TaskType.INTERVAL);

            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                strategy.getIntervalSeconds());
        }

        @Test
        @DisplayName("无效的时间表达式应该抛出异常")
        void invalidTimeExpressionShouldThrowException() {
            // Given
            ScheduleStrategy strategy = new ScheduleStrategy("invalid", TaskType.ONCE);

            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                strategy.getExecuteTime());
        }
    }

    @Nested
    @DisplayName("值对象特性测试")
    class ValueObjectTests {

        @Test
        @DisplayName("相同内容的策略应该相等")
        void strategiesWithSameContentShouldBeEqual() {
            // Given
            ScheduleStrategy strategy1 = ScheduleStrategy.cron("0 0 12 * * ?");
            ScheduleStrategy strategy2 = ScheduleStrategy.cron("0 0 12 * * ?");

            // When & Then
            assertEquals(strategy1, strategy2);
            assertEquals(strategy1.hashCode(), strategy2.hashCode());
        }

        @Test
        @DisplayName("不同内容的策略应该不相等")
        void strategiesWithDifferentContentShouldNotBeEqual() {
            // Given
            ScheduleStrategy strategy1 = ScheduleStrategy.cron("0 0 12 * * ?");
            ScheduleStrategy strategy2 = ScheduleStrategy.cron("0 0 18 * * ?");

            // When & Then
            assertNotEquals(strategy1, strategy2);
        }

        @Test
        @DisplayName("不同任务类型的策略应该不相等")
        void strategiesWithDifferentTaskTypesShouldNotBeEqual() {
            // Given
            ScheduleStrategy cronStrategy = ScheduleStrategy.cron("0 0 12 * * ?");
            ScheduleStrategy onceStrategy = ScheduleStrategy.once(Instant.now().plusSeconds(3600));

            // When & Then
            assertNotEquals(cronStrategy, onceStrategy);
        }

        @Test
        @DisplayName("toString 应该包含关键信息")
        void toStringShouldContainKeyInformation() {
            // Given
            ScheduleStrategy strategy = ScheduleStrategy.cron("0 0 12 * * ?");

            // When
            String toString = strategy.toString();

            // Then
            assertTrue(toString.contains("0 0 12 * * ?"));
            assertTrue(toString.contains("CRON"));
        }
    }
}
