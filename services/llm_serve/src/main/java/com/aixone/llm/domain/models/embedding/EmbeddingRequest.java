package com.aixone.llm.domain.models.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * 向量生成请求对象，对应 OpenAI Embedding API 请求结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {
    /**
     * 输入内容，支持字符串或字符串列表
     */
    private List<String> input;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 用户标识（可选）
     */
    private String user;
    /**
     * 编码格式（可选）
     */
    private String encodingFormat;
    /**
     * 维度（可选）
     */
    private Integer dimensions;
    /**
     * 额外参数（可选）
     */
    private Map<String, Object> extraParams;
} 