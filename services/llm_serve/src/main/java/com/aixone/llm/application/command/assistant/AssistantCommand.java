package com.aixone.llm.application.command.assistant;

import com.aixone.llm.domain.models.values.config.AssistantCapability;
import com.aixone.llm.domain.models.values.config.ToolConfig;
import lombok.Data;
import java.util.List;

@Data
public class AssistantCommand {
    private String name;
    private String userId;
    private String modelId;
    private List<ToolConfig> toolConfigs;
    private AssistantCapability capability;
} 