package com.aixone.llm.domain.models.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.aixone.llm.domain.models.BaseModelResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse implements BaseModelResponse {
    /**
     * 响应唯一ID
     */
    private String id;
    /**
     * 生成内容的选项列表，流式和非流式都在此返回
     */
    private List<Choice> choices;
    /**
     * 创建时间戳（秒）
     */
    private Long created;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 系统指纹
     */
    private String system_fingerprint;
    /**
     * 对象类型，如 chat.completion 或 chat.completion.chunk
     */
    private String object;
    /**
     * token 用量统计
     */
    private Usage usage;
    /**
     * 兼容原有简单返回
     */
    private String result;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        /**
         * 生成结束原因
         */
        private String finish_reason;
        /**
         * 选项索引
         */
        private Integer index;
        /**
         * 非流式响应时，完整消息内容
         */
        private Message message;
        /**
         * 流式响应时，增量内容
         */
        private Delta delta;
        /**
         * token 概率信息
         */
        private Logprobs logprobs;
    }

    /**
     * 流式响应时的增量内容结构
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Delta {
        /**
         * 角色（如 assistant）
         */
        private String role;
        /**
         * 增量内容
         */
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String content;
        private String reasoning_content;
        private List<ToolCall> tool_calls;
        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Function {
        private String name;
        private String arguments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Logprobs {
        private List<LogprobContent> content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogprobContent {
        private String token;
        private Double logprob;
        private List<Integer> bytes;
        private List<TopLogprob> top_logprobs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TopLogprob {
        private String token;
        private Double logprob;
        private List<Integer> bytes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private Integer completion_tokens;
        private Integer prompt_tokens;
        private Integer prompt_cache_hit_tokens;
        private Integer prompt_cache_miss_tokens;
        private Integer total_tokens;
        private CompletionTokensDetails completion_tokens_details;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompletionTokensDetails {
        private Integer reasoning_tokens;
    }
} 