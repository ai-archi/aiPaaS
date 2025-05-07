package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ToolCallResult {
    private String toolName;
    private String result;
    private boolean success;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
} 