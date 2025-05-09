package com.aixone.llm.domain.models.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.aixone.llm.domain.models.BaseModelResponse;

/**
 * 向量生成响应对象，对应 OpenAI Embedding API 响应结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse implements BaseModelResponse {
    /**
     * 响应唯一ID
     */
    private String id;
    /**
     * 对象类型，如 list
     */
    private String object;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 向量数据列表
     */
    private List<EmbeddingData> data;
    /**
     * token 用量统计
     */
    private Usage usage;
    /**
     * 创建时间戳（秒）
     */
    private Long created;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingData {
        /**
         * 向量索引
         */
        private Integer index;
        /**
         * 向量本身
         */
        private List<Float> embedding;
        /**
         * 输入内容类型（可选）
         */
        private String object;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private Integer promptTokens;
        private Integer totalTokens;
    }
}
