package com.aixone.workbench.message.domain.service;

import com.aixone.workbench.message.application.dto.MessageDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * 消息聚合领域服务
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
public interface MessageAggregationService {
    
    Page<MessageDTO> getMessages(UUID userId, UUID tenantId, int page, int size);
    
    void markAsRead(UUID messageId);
    
    void deleteMessage(UUID messageId);
}

