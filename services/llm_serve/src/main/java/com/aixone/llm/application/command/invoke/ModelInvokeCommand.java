package com.aixone.llm.application.command.invoke;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ModelInvokeCommand {
    private String userId;
    private String modelId;
    private String prompt;
    private List<Map<String, String>> messages;  // 用于对话模型
    private Map<String, Object> parameters;      // 模型参数
    private boolean stream;                      // 是否使用流式响应
} 