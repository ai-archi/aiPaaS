package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.util.Map;
import java.time.LocalDateTime;
@Data
@Builder
public class ToolConfig {
    private String toolType; // 例如: code_interpreter, retriever, function_call
    private Map<String, Object> config; // 工具相关配置参数
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
} 