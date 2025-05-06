package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolCallResult {
    private String toolName;
    private String result;
    private boolean success;
    // 可扩展更多字段
} 