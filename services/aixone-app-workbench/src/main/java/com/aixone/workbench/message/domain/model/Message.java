package com.aixone.workbench.message.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息聚合根
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "wb_message", indexes = {
    @Index(name = "idx_message_user_id", columnList = "user_id"),
    @Index(name = "idx_message_user_tenant", columnList = "user_id, tenant_id"),
    @Index(name = "idx_message_type", columnList = "message_type"),
    @Index(name = "idx_message_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Message implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @Column(name = "message_type", length = 50)
    private String messageType;
    
    @Column(name = "title", length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    
    @Column(name = "source", length = 100)
    private String source;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum MessageStatus {
        UNREAD,
        READ,
        PROCESSED
    }
}

