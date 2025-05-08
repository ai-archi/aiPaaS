package com.aixone.llm.application.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 模型查询参数对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelQuery {
    /** 是否激活 */
    private Boolean active;
    /** 提供商名称 */
    private String providerName;
    
    // 可扩展更多查询条件
} 