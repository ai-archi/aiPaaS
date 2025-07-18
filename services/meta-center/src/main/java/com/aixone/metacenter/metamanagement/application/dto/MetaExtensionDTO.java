package com.aixone.metacenter.metamanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据扩展点DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
public class MetaExtensionDTO {

    /** 唯一标识 */
    private Long id;

    /** 扩展点标识 */
    private String extensionKey;

    /** 扩展类型 */
    private String extensionType;

    /** 扩展值 */
    private Object extensionValue;

    /** 扩展处理器 */
    private String handler;

    /** 作用域 */
    private String scope;

    /** 是否启用 */
    private Boolean enabled;

    /** 版本号 */
    private String version;

    /** 扩展字段 */
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 关联的元数据对象ID */
    private Long metaObjectId;
} 