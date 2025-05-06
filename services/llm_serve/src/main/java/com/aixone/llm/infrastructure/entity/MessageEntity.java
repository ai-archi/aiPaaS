package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.values.config.ToolCallResult;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Table("messages")
public class MessageEntity {
    @Id
    private String id;
    
    @Version
    private Long version;
    
    private String threadId;
    private String role;
    private String content;
    private List<ToolCallResult> toolCalls;
    private Map<String, Object> metadata;
    private long createdAt;
    private ToolCallResult toolCallResult;
} 