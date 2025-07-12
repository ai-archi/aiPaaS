package com.aixone.eventcenter.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 调度任务仓库
 */
public interface ScheduleTaskRepository extends JpaRepository<ScheduleTask, Long> {
    // 可扩展自定义查询
    java.util.List<ScheduleTask> findByTenantId(String tenantId);
    java.util.Optional<ScheduleTask> findByTaskIdAndTenantId(Long taskId, String tenantId);
} 