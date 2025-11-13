package com.aixone.eventcenter.schedule.domain;

import com.aixone.common.ddd.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 任务日志仓库接口
 * 定义任务执行记录数据访问操作
 */
public interface TaskLogRepository extends Repository<TaskLog, Long> {
    
    /**
     * 根据任务ID查找执行记录
     */
    List<TaskLog> findByTaskIdAndTenantId(Long taskId, String tenantId);
    
    /**
     * 根据任务ID分页查询执行记录
     */
    Page<TaskLog> findByTaskIdAndTenantId(Long taskId, String tenantId, Pageable pageable);
    
    /**
     * 根据执行状态查找记录
     */
    List<TaskLog> findByStatusAndTenantId(TaskStatus status, String tenantId);
    
    /**
     * 根据执行节点查找记录
     */
    List<TaskLog> findByExecutorNodeAndTenantId(String executorNode, String tenantId);
    
    /**
     * 查找指定时间范围内的执行记录
     */
    List<TaskLog> findByStartTimeBetweenAndTenantId(Instant startTime, Instant endTime, String tenantId);
    
    /**
     * 查找任务的最新执行记录
     */
    Optional<TaskLog> findTopByTaskIdAndTenantIdOrderByStartTimeDesc(Long taskId, String tenantId);
    
    /**
     * 查找任务的成功执行记录
     */
    List<TaskLog> findByTaskIdAndStatusAndTenantId(Long taskId, TaskStatus status, String tenantId);
    
    /**
     * 统计任务执行次数
     */
    long countByTaskIdAndTenantId(Long taskId, String tenantId);
    
    /**
     * 统计任务成功执行次数
     */
    long countByTaskIdAndStatusAndTenantId(Long taskId, TaskStatus status, String tenantId);
    
    /**
     * 删除过期的执行记录
     */
    void deleteByCreateTimeBeforeAndTenantId(Instant expireTime, String tenantId);
    
    /**
     * 根据任务ID删除所有执行记录
     */
    void deleteByTaskIdAndTenantId(Long taskId, String tenantId);
    
    /**
     * 根据ID和租户ID查找任务日志
     */
    Optional<TaskLog> findByIdAndTenantId(Long id, String tenantId);
}
