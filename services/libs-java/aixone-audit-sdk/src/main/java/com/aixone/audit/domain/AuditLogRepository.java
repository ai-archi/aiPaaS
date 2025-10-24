package com.aixone.audit.domain;

import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.ddd.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志仓储接口
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public interface AuditLogRepository extends Repository<AuditLog, Long> {
    
    /**
     * 根据用户ID查询审计日志
     * 
     * @param userId 用户ID
     * @return 审计日志列表
     */
    List<AuditLog> findByUserId(String userId);
    
    /**
     * 根据操作类型查询审计日志
     * 
     * @param action 操作类型
     * @return 审计日志列表
     */
    List<AuditLog> findByAction(String action);
    
    /**
     * 根据操作结果查询审计日志
     * 
     * @param result 操作结果
     * @return 审计日志列表
     */
    List<AuditLog> findByResult(String result);
    
    /**
     * 根据时间范围查询审计日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和时间范围查询审计日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据操作类型和时间范围查询审计日志
     * 
     * @param action 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByActionAndTimestampBetween(String action, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 分页查询审计日志
     * 
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AuditLog> findPage(PageRequest pageRequest);
    
    /**
     * 根据用户ID分页查询审计日志
     * 
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AuditLog> findByUserIdPage(String userId, PageRequest pageRequest);
    
    /**
     * 根据操作类型分页查询审计日志
     * 
     * @param action 操作类型
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AuditLog> findByActionPage(String action, PageRequest pageRequest);
    
    /**
     * 根据时间范围分页查询审计日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AuditLog> findByTimestampBetweenPage(LocalDateTime startTime, LocalDateTime endTime, PageRequest pageRequest);
    
    /**
     * 根据多个条件分页查询审计日志
     * 
     * @param userId 用户ID（可选）
     * @param action 操作类型（可选）
     * @param result 操作结果（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AuditLog> findByConditionsPage(String userId, String action, String result, 
                                            LocalDateTime startTime, LocalDateTime endTime, 
                                            PageRequest pageRequest);
}
