package com.aixone.llm.domain.models.aggregates.assistant;

import com.aixone.llm.domain.models.values.config.AssistantCapability;
import com.aixone.llm.domain.models.values.config.ToolConfig;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Assistant {
    private String id;
    private String userId;
    private String modelId;
    private List<ToolConfig> toolConfigs;
    private AssistantCapability capability;
    // 可扩展更多字段
} 