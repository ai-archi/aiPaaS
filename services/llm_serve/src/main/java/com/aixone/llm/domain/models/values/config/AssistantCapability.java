package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.util.Set;
import java.time.LocalDateTime;
@Data
@Builder
public class AssistantCapability {
    private Set<String> supportedFeatures; // 例如: chat, tool_call, code_interpreter
    private int maxThreads;
    private int maxMessages;
    private boolean toolCallSupported;
    private boolean codeInterpreterSupported;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
    // 可扩展更多能力字段
} 