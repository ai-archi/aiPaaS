package com.aixone.eventcenter.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 调度节点仓库
 */
public interface SchedulerRepository extends JpaRepository<Scheduler, Long> {
    // 可扩展自定义查询
} 