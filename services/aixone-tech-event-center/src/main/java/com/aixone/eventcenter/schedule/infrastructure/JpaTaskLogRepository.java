package com.aixone.eventcenter.schedule.infrastructure;

import com.aixone.eventcenter.schedule.domain.TaskLog;
import com.aixone.eventcenter.schedule.domain.TaskLogRepository;
import com.aixone.eventcenter.schedule.domain.TaskStatus;
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
 * 任务日志JPA仓库实现
 */
@Repository
public interface JpaTaskLogRepository extends JpaRepository<TaskLog, Long>, TaskLogRepository {
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.tenantId = :tenantId ORDER BY tl.startTime DESC")
    List<TaskLog> findByTaskIdAndTenantId(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.tenantId = :tenantId ORDER BY tl.startTime DESC")
    Page<TaskLog> findByTaskIdAndTenantId(@Param("taskId") Long taskId, @Param("tenantId") String tenantId, Pageable pageable);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.status = :status AND tl.tenantId = :tenantId")
    List<TaskLog> findByStatusAndTenantId(@Param("status") TaskStatus status, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.executorNode = :executorNode AND tl.tenantId = :tenantId")
    List<TaskLog> findByExecutorNodeAndTenantId(@Param("executorNode") String executorNode, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.startTime BETWEEN :startTime AND :endTime AND tl.tenantId = :tenantId")
    List<TaskLog> findByStartTimeBetweenAndTenantId(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.tenantId = :tenantId ORDER BY tl.startTime DESC")
    Optional<TaskLog> findTopByTaskIdAndTenantIdOrderByStartTimeDesc(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.status = :status AND tl.tenantId = :tenantId")
    List<TaskLog> findByTaskIdAndStatusAndTenantId(@Param("taskId") Long taskId, @Param("status") TaskStatus status, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT COUNT(tl) FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.tenantId = :tenantId")
    long countByTaskIdAndTenantId(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT COUNT(tl) FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.status = :status AND tl.tenantId = :tenantId")
    long countByTaskIdAndStatusAndTenantId(@Param("taskId") Long taskId, @Param("status") TaskStatus status, @Param("tenantId") String tenantId);
    
    @Override
    @Query("DELETE FROM TaskLog tl WHERE tl.createTime < :expireTime AND tl.tenantId = :tenantId")
    void deleteByCreateTimeBeforeAndTenantId(@Param("expireTime") Instant expireTime, @Param("tenantId") String tenantId);
    
    @Override
    @Query("DELETE FROM TaskLog tl WHERE tl.taskId = :taskId AND tl.tenantId = :tenantId")
    void deleteByTaskIdAndTenantId(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT tl FROM TaskLog tl WHERE tl.id = :id AND tl.tenantId = :tenantId")
    Optional<TaskLog> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
}
