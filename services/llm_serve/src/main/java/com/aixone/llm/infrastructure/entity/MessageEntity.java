package com.aixone.llm.infrastructure.entity;

import com.aixone.llm.domain.models.entities.message.Message;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("messages")
public class MessageEntity {
    @Id
    private String id;
    private String content;
    private String role;
    private String name;
    private String toolCallId;
    private String tenantId;
    /**
     * 创建时间，秒级时间戳
     */
    private Long createdAt;

    public static MessageEntity fromDomain(Message message) {
        return MessageEntity.builder()
                .id(message.getId())
                .content(message.getContentAsString())
                .role(message.getRole())
                .name(message.getName())
                .toolCallId(message.getToolCallId())
                .tenantId(message.getTenantId())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public Message toDomain() {
        return Message.builder()
                .id(this.id)
                .content(this.content)
                .role(this.role)
                .name(this.name)
                .toolCallId(this.toolCallId)
                .tenantId(this.tenantId)
                .createdAt(this.createdAt)
                .build();
    }
} 