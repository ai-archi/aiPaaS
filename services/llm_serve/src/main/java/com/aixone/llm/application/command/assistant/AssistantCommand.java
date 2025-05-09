package com.aixone.llm.application.command.assistant;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.AssistantCapability;
import com.aixone.llm.domain.models.values.config.ToolConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantCommand extends ModelRequest {
    private String assistantId;
    private String action;
    private String user;
    private String name;
    private String userId;
    private String modelId;
    private List<ToolConfig> toolConfigs;
    private AssistantCapability capability;

    public ModelRequest toModelRequest() {
        return  ModelRequest.builder()
                .model(getModel())
                .messages(getMessages())
                .maxTokens(getMaxTokens())
                .temperature(getTemperature())
                .topP(getTopP())
                .stream(isStream())
                .streamOptions(getStreamOptions())
                .build();
    }

    public com.aixone.llm.domain.models.aggregates.assistant.Assistant toAssistant(String tenantId) {
        com.aixone.llm.domain.models.aggregates.assistant.Assistant assistant = new com.aixone.llm.domain.models.aggregates.assistant.Assistant();
        assistant.setId(this.assistantId);
        assistant.setName(this.name);
        assistant.setUserId(this.userId);
        assistant.setModelId(this.modelId);
        assistant.setToolConfigs(this.toolConfigs);
        assistant.setCapability(this.capability);
        assistant.setTenantId(tenantId);
        assistant.setCreatedAt(java.time.LocalDateTime.now());
        assistant.setUpdatedAt(java.time.LocalDateTime.now());
        return assistant;
    }
} 