package com.aixone.metacenter.metamanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 元数据规则DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
public class MetaRuleDTO {

    /** 唯一标识 */
    private Long id;

    /** 规则名称 */
    private String name;

    /** 规则类型 */
    private String ruleType;

    /** 规则组 */
    private String ruleGroup;

    /** 规则表达式 */
    private String expression;

    /** 错误消息 */
    private String errorMessage;

    /** 是否启用 */
    private Boolean enabled;

    /** 优先级 */
    private Integer priority;

    /** 触发条件 */
    private String trigger;

    /** 执行动作 */
    private String action;

    /** 规则流程 */
    private Map<String, Object> flow = new HashMap<>();

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