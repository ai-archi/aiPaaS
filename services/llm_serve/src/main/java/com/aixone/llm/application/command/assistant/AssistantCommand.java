package com.aixone.llm.application.command.assistant;

import com.aixone.llm.domain.models.assistant.Assistant;
import com.aixone.llm.domain.models.assistant.AssistantCapability;
import com.aixone.llm.domain.models.assistant.ToolConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantCommand {
    private String assistantId;
    private String action;
    private String user;
    private String name;
    private String userId;
    private String modelId;
    private List<ToolConfig> toolConfigs;
    private AssistantCapability capability;
    private String description;
    private boolean active;

    public Assistant toAssistant(String tenantId) {
        Assistant assistant = new Assistant();
        assistant.setId(this.assistantId);
        assistant.setName(this.name);
        assistant.setUserId(this.userId);
        assistant.setModelId(this.modelId);
        assistant.setToolConfigs(this.toolConfigs);
        assistant.setCapability(this.capability);
        assistant.setTenantId(tenantId);
        assistant.setDescription(this.description);
        assistant.setActive(this.active);
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setUpdatedAt(LocalDateTime.now());
        return assistant;
    }
} 