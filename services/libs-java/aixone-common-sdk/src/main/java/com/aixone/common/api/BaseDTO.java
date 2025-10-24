package com.aixone.common.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通用DTO基类
 * 提供通用的字段和方法
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {
    
    /** 租户ID */
    private String tenantId;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /** 创建人 */
    private String createdBy;
    
    /** 更新人 */
    private String updatedBy;
    
    /**
     * 构造函数
     * 
     * @param tenantId 租户ID
     */
    public BaseDTO(String tenantId) {
        this.tenantId = tenantId;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 设置创建信息
     * 
     * @param createdBy 创建人
     */
    public void setCreatedInfo(String createdBy) {
        this.createdBy = createdBy;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 设置更新信息
     * 
     * @param updatedBy 更新人
     */
    public void setUpdatedInfo(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedTime = LocalDateTime.now();
    }
}
