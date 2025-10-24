package com.aixone.eventcenter.schedule.infrastructure;

import com.aixone.eventcenter.schedule.domain.Task;
import com.aixone.eventcenter.schedule.domain.TaskRepository;
import com.aixone.eventcenter.schedule.domain.TaskStatus;
import com.aixone.eventcenter.schedule.domain.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 任务JPA仓库实现
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<Task, Long>, TaskRepository {
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.taskName = :taskName AND t.tenantId = :tenantId")
    Optional<Task> findByTaskNameAndTenantId(@Param("taskName") String taskName, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.tenantId = :tenantId")
    List<Task> findByStatusAndTenantId(@Param("status") TaskStatus status, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.taskType = :taskType AND t.tenantId = :tenantId")
    List<Task> findByTaskTypeAndTenantId(@Param("taskType") TaskType taskType, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.enabled = true AND t.tenantId = :tenantId")
    List<Task> findByEnabledTrueAndTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.status = 'PENDING' AND t.enabled = true AND t.nextExecuteTime <= :currentTime AND t.tenantId = :tenantId")
    List<Task> findPendingTasksForExecution(@Param("currentTime") Instant currentTime, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.executorService = :executorService AND t.tenantId = :tenantId")
    List<Task> findByExecutorServiceAndTenantId(@Param("executorService") String executorService, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.tenantId = :tenantId")
    Page<Task> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.taskName LIKE %:taskName% AND t.tenantId = :tenantId")
    Page<Task> findByTaskNameContainingAndTenantId(@Param("taskName") String taskName, @Param("tenantId") String tenantId, Pageable pageable);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.tenantId = :tenantId")
    Page<Task> findByStatusAndTenantId(@Param("status") TaskStatus status, @Param("tenantId") String tenantId, Pageable pageable);
    
    @Override
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status AND t.tenantId = :tenantId")
    long countByStatusAndTenantId(@Param("status") TaskStatus status, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT COUNT(t) FROM Task t WHERE t.enabled = true AND t.tenantId = :tenantId")
    long countByEnabledTrueAndTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("DELETE FROM Task t WHERE t.taskType = 'ONCE' AND t.status IN ('SUCCESS', 'FAIL') AND t.lastExecuteTime < :expireTime AND t.tenantId = :tenantId")
    void deleteExpiredTasks(@Param("expireTime") Instant expireTime, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.tenantId = :tenantId")
    Optional<Task> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
}
