package com.aixone.audit.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA审计日志仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    
    /**
     * 根据用户ID查询审计日志
     */
    List<AuditLogEntity> findByUserId(String userId);
    
    /**
     * 根据操作类型查询审计日志
     */
    List<AuditLogEntity> findByAction(String action);
    
    /**
     * 根据操作结果查询审计日志
     */
    List<AuditLogEntity> findByResult(String result);
    
    /**
     * 根据时间范围查询审计日志
     */
    List<AuditLogEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和时间范围查询审计日志
     */
    List<AuditLogEntity> findByUserIdAndTimestampBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据操作类型和时间范围查询审计日志
     */
    List<AuditLogEntity> findByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID分页查询审计日志
     */
    Page<AuditLogEntity> findByUserId(String userId, Pageable pageable);
    
    /**
     * 根据操作类型分页查询审计日志
     */
    Page<AuditLogEntity> findByAction(String action, Pageable pageable);
    
    /**
     * 根据时间范围分页查询审计日志
     */
    Page<AuditLogEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据多个条件分页查询审计日志
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:result IS NULL OR a.result = :result) AND " +
           "(:startTime IS NULL OR a.timestamp >= :startTime) AND " +
           "(:endTime IS NULL OR a.timestamp <= :endTime) AND " +
           "(:tenantId IS NULL OR a.tenantId = :tenantId)")
    Page<AuditLogEntity> findByConditions(@Param("userId") String userId,
                                         @Param("action") String action,
                                         @Param("result") String result,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("tenantId") String tenantId,
                                         Pageable pageable);
}
