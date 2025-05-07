package com.aixone.llm.domain.models.aggregates.assistant;

import com.aixone.llm.domain.models.values.config.AssistantCapability;
import com.aixone.llm.domain.models.values.config.ToolConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assistant {
    private String id;
    private String name;
    private String userId;
    
    private String modelId;
    private List<ToolConfig> toolConfigs;
    private AssistantCapability capability;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tenantId;
    private boolean deleted;
    // 可扩展更多字段
} 