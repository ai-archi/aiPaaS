package com.aixone.eventcenter.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 * 测试环境 Quartz 配置
 * 使用内存存储，避免数据库依赖
 */
@TestConfiguration
public class TestQuartzConfig {
    
    /**
     * 测试环境 Quartz 调度器工厂Bean
     */
    @Bean
    @Primary
    public SchedulerFactoryBean testSchedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(testQuartzProperties());
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        return factory;
    }
    
    /**
     * 测试环境 Quartz 调度器
     */
    @Bean
    @Primary
    public Scheduler testScheduler() throws SchedulerException {
        return testSchedulerFactoryBean().getScheduler();
    }
    
    /**
     * 测试环境 Quartz 属性配置
     */
    private Properties testQuartzProperties() {
        Properties properties = new Properties();
        
        // 调度器配置
        properties.setProperty("org.quartz.scheduler.instanceName", "TestScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        
        // 线程池配置
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "3");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        
        // 作业存储配置 - 使用内存存储
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        
        return properties;
    }
}
