package com.aixone.workbench.message.infrastructure.service;

import com.aixone.workbench.message.application.dto.MessageDTO;
import com.aixone.workbench.message.domain.model.Message;
import com.aixone.workbench.message.domain.repository.MessageRepository;
import com.aixone.workbench.message.domain.service.MessageAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 消息聚合服务实现
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageAggregationServiceImpl implements MessageAggregationService {
    
    private final MessageRepository messageRepository;
    
    @Override
    public Page<MessageDTO> getMessages(UUID userId, UUID tenantId, int page, int size) {
        log.info("获取用户消息: userId={}, tenantId={}, page={}, size={}", userId, tenantId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                userId, tenantId, pageable);
        
        return messages.map(this::toDTO);
    }
    
    @Override
    @Transactional
    public void markAsRead(UUID messageId) {
        log.info("标记消息为已读: messageId={}", messageId);
        
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setStatus(Message.MessageStatus.READ);
            messageRepository.save(message);
        });
    }
    
    @Override
    @Transactional
    public void deleteMessage(UUID messageId) {
        log.info("删除消息: messageId={}", messageId);
        messageRepository.deleteById(messageId);
    }
    
    private MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .userId(message.getUserId())
                .tenantId(message.getTenantId())
                .messageType(message.getMessageType())
                .title(message.getTitle())
                .content(message.getContent())
                .status(message.getStatus() != null ? message.getStatus().toString() : "UNREAD")
                .source(message.getSource())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

