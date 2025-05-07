package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.values.config.ToolCallResult;
import com.aixone.llm.domain.models.entities.message.Message;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

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

    public static MessageEntity fromDomain(Message message) {
        return MessageEntity.builder()
                .id(message.getId())
                .threadId(message.getThreadId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getTimestamp() != null ? message.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toEpochSecond() : 0)
                .build();
    }

    public Message toDomain() {
        return Message.builder()
                .id(this.id)
                .threadId(this.threadId)
                .role(this.role)
                .content(this.content)
                .timestamp(this.createdAt > 0 ? LocalDateTime.ofEpochSecond(this.createdAt, 0, java.time.ZoneOffset.ofHours(8)) : null)
                .build();
    }
} 