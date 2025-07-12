package com.aixone.eventcenter.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务执行日志仓库
 */
public interface JobLogRepository extends JpaRepository<JobLog, Long> {
    // 可扩展自定义查询
} 