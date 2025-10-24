package com.aixone.eventcenter.schedule.domain;

import com.aixone.common.ddd.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 任务仓库接口
 * 定义任务数据访问操作
 */
public interface TaskRepository extends Repository<Task, Long> {
    
    /**
     * 根据任务ID和租户ID查找任务
     */
    Optional<Task> findByIdAndTenantId(Long taskId, String tenantId);
    
    /**
     * 根据任务名称查找任务
     */
    Optional<Task> findByTaskNameAndTenantId(String taskName, String tenantId);
    
    /**
     * 根据任务状态查找任务
     */
    List<Task> findByStatusAndTenantId(TaskStatus status, String tenantId);
    
    /**
     * 根据任务类型查找任务
     */
    List<Task> findByTaskTypeAndTenantId(TaskType taskType, String tenantId);
    
    /**
     * 查找启用的任务
     */
    List<Task> findByEnabledTrueAndTenantId(String tenantId);
    
    /**
     * 查找需要执行的任务（状态为PENDING且下次执行时间已到）
     */
    List<Task> findPendingTasksForExecution(Instant currentTime, String tenantId);
    
    /**
     * 根据执行微服务查找任务
     */
    List<Task> findByExecutorServiceAndTenantId(String executorService, String tenantId);
    
    /**
     * 分页查询任务
     */
    Page<Task> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * 根据任务名称模糊查询
     */
    Page<Task> findByTaskNameContainingAndTenantId(String taskName, String tenantId, Pageable pageable);
    
    /**
     * 根据状态分页查询
     */
    Page<Task> findByStatusAndTenantId(TaskStatus status, String tenantId, Pageable pageable);
    
    /**
     * 统计任务数量
     */
    long countByStatusAndTenantId(TaskStatus status, String tenantId);
    
    /**
     * 统计启用的任务数量
     */
    long countByEnabledTrueAndTenantId(String tenantId);
    
    /**
     * 删除过期的任务（一次性任务且已执行完成）
     */
    void deleteExpiredTasks(Instant expireTime, String tenantId);
}
