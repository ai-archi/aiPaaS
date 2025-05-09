package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.assistant.AssistantCapability;
import com.aixone.llm.domain.models.assistant.ToolConfig;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@Builder
@Table("assistants")
public class AssistantEntity {
    @Id
    private String id;
    
    @Version
    private Long version;
    
    private String name;
    private String description;
    private String modelId;
    private AssistantCapability capability;
    private ToolConfig toolConfig;
    private List<ToolConfig> tools;
    private boolean active;
    private long createdAt;
    private long updatedAt;
} 