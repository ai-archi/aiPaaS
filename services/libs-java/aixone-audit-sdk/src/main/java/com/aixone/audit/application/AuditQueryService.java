package com.aixone.audit.application;

import com.aixone.audit.domain.AuditLog;
import com.aixone.audit.domain.AuditLogRepository;
import com.aixone.audit.interfaces.dto.AuditLogDTO;
import com.aixone.audit.interfaces.dto.AuditLogQueryDTO;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.exception.NotFoundException;
import com.aixone.common.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计查询服务
 * 提供审计日志的查询功能
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Service
@Transactional(readOnly = true)
public class AuditQueryService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Autowired
    public AuditQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    /**
     * 根据ID获取审计日志
     * 
     * @param id 审计日志ID
     * @return 审计日志DTO
     */
    public AuditLogDTO getById(Long id) {
        ValidationUtils.notNull(id, "审计日志ID不能为空");
        
        AuditLog auditLog = auditLogRepository.getById(id);
        if (auditLog == null) {
            throw new NotFoundException("审计日志不存在: " + id);
        }
        
        return convertToDTO(auditLog);
    }
    
    /**
     * 分页查询审计日志
     * 
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AuditLogDTO> getAuditLogs(PageRequest pageRequest) {
        ValidationUtils.notNull(pageRequest, "分页请求不能为空");
        
        PageResult<AuditLog> pageResult = auditLogRepository.findPage(pageRequest);
        List<AuditLogDTO> dtoList = pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), pageResult.getPageNum(), 
                               pageResult.getPageSize(), dtoList);
    }
    
    /**
     * 根据用户ID分页查询审计日志
     * 
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AuditLogDTO> getAuditLogsByUserId(String userId, PageRequest pageRequest) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        ValidationUtils.notNull(pageRequest, "分页请求不能为空");
        
        PageResult<AuditLog> pageResult = auditLogRepository.findByUserIdPage(userId, pageRequest);
        List<AuditLogDTO> dtoList = pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), pageResult.getPageNum(), 
                               pageResult.getPageSize(), dtoList);
    }
    
    /**
     * 根据操作类型分页查询审计日志
     * 
     * @param action 操作类型
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AuditLogDTO> getAuditLogsByAction(String action, PageRequest pageRequest) {
        ValidationUtils.notBlank(action, "操作类型不能为空");
        ValidationUtils.notNull(pageRequest, "分页请求不能为空");
        
        PageResult<AuditLog> pageResult = auditLogRepository.findByActionPage(action, pageRequest);
        List<AuditLogDTO> dtoList = pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), pageResult.getPageNum(), 
                               pageResult.getPageSize(), dtoList);
    }
    
    /**
     * 根据时间范围分页查询审计日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    public PageResult<AuditLogDTO> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, 
                                                          PageRequest pageRequest) {
        ValidationUtils.notNull(startTime, "开始时间不能为空");
        ValidationUtils.notNull(endTime, "结束时间不能为空");
        ValidationUtils.notNull(pageRequest, "分页请求不能为空");
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        PageResult<AuditLog> pageResult = auditLogRepository.findByTimestampBetweenPage(startTime, endTime, pageRequest);
        List<AuditLogDTO> dtoList = pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), pageResult.getPageNum(), 
                               pageResult.getPageSize(), dtoList);
    }
    
    /**
     * 根据多个条件分页查询审计日志
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<AuditLogDTO> getAuditLogsByConditions(AuditLogQueryDTO query) {
        ValidationUtils.notNull(query, "查询条件不能为空");
        
        PageRequest pageRequest = new PageRequest(query.getPageNum(), query.getPageSize(), 
                                                query.getSortBy(), query.getSortDirection());
        
        PageResult<AuditLog> pageResult = auditLogRepository.findByConditionsPage(
            query.getUserId(),
            query.getAction(),
            query.getResult(),
            query.getStartTime(),
            query.getEndTime(),
            pageRequest
        );
        
        List<AuditLogDTO> dtoList = pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), pageResult.getPageNum(), 
                               pageResult.getPageSize(), dtoList);
    }
    
    /**
     * 获取用户最近的操作日志
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 审计日志列表
     */
    public List<AuditLogDTO> getRecentAuditLogsByUserId(String userId, int limit) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        ValidationUtils.isTrue(limit > 0, "限制数量必须大于0");
        
        PageRequest pageRequest = new PageRequest(1, limit, "timestamp", "desc");
        PageResult<AuditLog> pageResult = auditLogRepository.findByUserIdPage(userId, pageRequest);
        
        return pageResult.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 统计审计日志数量
     * 
     * @return 审计日志总数
     */
    public long countAuditLogs() {
        return auditLogRepository.count();
    }
    
    /**
     * 统计用户审计日志数量
     * 
     * @param userId 用户ID
     * @return 审计日志数量
     */
    public long countAuditLogsByUserId(String userId) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        return auditLogRepository.findByUserId(userId).size();
    }
    
    /**
     * 转换为DTO
     * 
     * @param auditLog 审计日志实体
     * @return 审计日志DTO
     */
    private AuditLogDTO convertToDTO(AuditLog auditLog) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLog.getId());
        dto.setTenantId(auditLog.getTenantId());
        dto.setUserId(auditLog.getUserId());
        dto.setAction(auditLog.getAction());
        dto.setResource(auditLog.getResource());
        dto.setResult(auditLog.getResult());
        dto.setTimestamp(auditLog.getTimestamp());
        dto.setClientIp(auditLog.getClientIp());
        dto.setUserAgent(auditLog.getUserAgent());
        dto.setDetails(auditLog.getDetails());
        dto.setErrorMessage(auditLog.getErrorMessage());
        dto.setSessionId(auditLog.getSessionId());
        return dto;
    }
}
