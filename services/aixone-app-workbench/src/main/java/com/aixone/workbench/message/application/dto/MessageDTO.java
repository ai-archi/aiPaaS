package com.aixone.workbench.message.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private String messageType;
    private String title;
    private String content;
    private String status;
    private String source;
    private LocalDateTime createdAt;
}

