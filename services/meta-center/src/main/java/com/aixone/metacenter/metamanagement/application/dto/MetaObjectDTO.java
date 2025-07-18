package com.aixone.metacenter.metamanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元数据对象DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
public class MetaObjectDTO {

    /** 唯一标识 */
    private Long id;

    /** 元数据名称 */
    private String name;

    /** 对象类型 */
    private String objectType;

    /** 主类型（业务/技术/管理/参考） */
    private String type;

    /** 子类型 */
    private String subType;

    /** 描述 */
    private String description;

    /** 标签 */
    private String tags;

    /** 生命周期状态 */
    private String lifecycle;

    /** 运行状态 */
    private String status;

    /** 版本号 */
    private Integer version;

    /** 责任人 */
    private String owner;

    /** 质量分数 */
    private Integer qualityScore;

    /** 合规等级 */
    private String complianceLevel;

    /** 租户ID */
    private String tenantId;

    /** 多语言支持 */
    private Map<String, String> i18n = new HashMap<>();

    /** 扩展字段 */
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建人 */
    private String createdBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新人 */
    private String updatedBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 属性列表 */
    private List<MetaAttributeDTO> attributes;

    /** 关系列表 */
    private List<MetaRelationDTO> relations;

    /** 规则列表 */
    private List<MetaRuleDTO> rules;

    /** 扩展点列表 */
    private List<MetaExtensionDTO> extensions;
} 