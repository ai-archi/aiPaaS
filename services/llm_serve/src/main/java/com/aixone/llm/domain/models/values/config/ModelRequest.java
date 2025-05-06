package com.aixone.llm.domain.models.values.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
    private String model; // 模型名称
    private List<Map<String, Object>> messages; // 对话消息列表
    private String prompt; // 补全/摘要等场景的输入
    private Integer maxTokens; // 最大生成token数
    private Double temperature; // 采样温度
    private Double topP; // top_p采样
    private Boolean stream; // 是否流式返回
    private Map<String, Object> extraParams; // 其他可扩展参数
} 