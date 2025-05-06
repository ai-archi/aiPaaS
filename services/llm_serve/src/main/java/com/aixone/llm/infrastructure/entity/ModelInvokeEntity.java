package com.aixone.llm.infrastructure.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("model_invokes")
public class ModelInvokeEntity {
    @Id
    private Long id;
    
    private String userId;
    private String modelId;
    private String requestId;
    private String prompt;
    private String response;
    private Long usedTokens;
    private Long promptTokens;
    private Long completionTokens;
    private Double totalTime;
    private String finishReason;
    private Boolean isError;
    private String errorMessage;
    private LocalDateTime invokeTime;
    private LocalDateTime createdAt;
} 