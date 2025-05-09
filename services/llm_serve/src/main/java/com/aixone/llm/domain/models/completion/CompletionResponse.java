package com.aixone.llm.domain.models.completion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;
import com.aixone.llm.domain.models.BaseModelResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse implements BaseModelResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("choices")
    private List<Choice> choices;
    @JsonProperty("created")
    private Long created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
    @JsonProperty("object")
    private String object;
    @JsonProperty("usage")
    private Usage usage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        @JsonProperty("text")
        private String text;
        @JsonProperty("index")
        private Integer index;
        @JsonProperty("logprobs")
        private Logprobs logprobs;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Logprobs {
        @JsonProperty("tokens")
        private List<String> tokens;
        @JsonProperty("token_logprobs")
        private List<Double> tokenLogprobs;
        @JsonProperty("top_logprobs")
        private List<Object> topLogprobs;
        @JsonProperty("text_offset")
        private List<Integer> textOffset;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
} 