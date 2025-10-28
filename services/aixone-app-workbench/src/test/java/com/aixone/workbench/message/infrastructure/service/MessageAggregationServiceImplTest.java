package com.aixone.workbench.message.infrastructure.service;

import com.aixone.workbench.message.application.dto.MessageDTO;
import com.aixone.workbench.message.domain.model.Message;
import com.aixone.workbench.message.domain.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 消息聚合服务测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("消息聚合服务测试")
class MessageAggregationServiceImplTest {
    
    @Mock
    private MessageRepository messageRepository;
    
    @InjectMocks
    private MessageAggregationServiceImpl messageAggregationService;
    
    private UUID userId;
    private UUID tenantId;
    private UUID messageId;
    private Message message;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        
        message = Message.builder()
                .id(messageId)
                .userId(userId)
                .tenantId(tenantId)
                .messageType("NOTIFICATION")
                .title("测试消息")
                .content("测试内容")
                .status(Message.MessageStatus.UNREAD)
                .source("test")
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    @DisplayName("测试获取用户消息")
    void testGetMessages() {
        // Given
        Page<Message> page = new PageImpl<>(List.of(message));
        when(messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                eq(userId), eq(tenantId), any()))
                .thenReturn(page);
        
        // When
        Page<MessageDTO> result = messageAggregationService.getMessages(userId, tenantId, 0, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("测试消息");
    }
    
    @Test
    @DisplayName("测试标记消息为已读")
    void testMarkAsRead() {
        // Given
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        
        // When
        messageAggregationService.markAsRead(messageId);
        
        // Then
        assertThat(message.getStatus()).isEqualTo(Message.MessageStatus.READ);
        verify(messageRepository).save(message);
    }
    
    @Test
    @DisplayName("测试删除消息")
    void testDeleteMessage() {
        // When
        messageAggregationService.deleteMessage(messageId);
        
        // Then
        verify(messageRepository).deleteById(messageId);
    }
    
    @Test
    @DisplayName("测试获取用户消息 - 分页")
    void testGetMessages_Pagination() {
        // Given
        Page<Message> page = new PageImpl<>(List.of(message), PageRequest.of(0, 10), 1);
        when(messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                eq(userId), eq(tenantId), any()))
                .thenReturn(page);
        
        // When
        Page<MessageDTO> result = messageAggregationService.getMessages(userId, tenantId, 0, 10);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("测试获取用户消息 - 空列表")
    void testGetMessages_EmptyList() {
        // Given
        Page<Message> page = new PageImpl<>(List.of());
        when(messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                eq(userId), eq(tenantId), any()))
                .thenReturn(page);
        
        // When
        Page<MessageDTO> result = messageAggregationService.getMessages(userId, tenantId, 0, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    @DisplayName("测试标记消息为已读 - 消息不存在")
    void testMarkAsRead_MessageNotFound() {
        // Given
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());
        
        // When
        messageAggregationService.markAsRead(messageId);
        
        // Then
        verify(messageRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("测试标记消息为已读 - 已是已读状态")
    void testMarkAsRead_AlreadyRead() {
        // Given
        message.setStatus(Message.MessageStatus.READ);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        
        // When
        messageAggregationService.markAsRead(messageId);
        
        // Then
        assertThat(message.getStatus()).isEqualTo(Message.MessageStatus.READ);
        verify(messageRepository).save(message);
    }
    
    @Test
    @DisplayName("测试删除消息 - 异常处理")
    void testDeleteMessage_Exception() {
        // Given
        doThrow(new RuntimeException("Database error")).when(messageRepository).deleteById(messageId);
        
        // When & Then
        assertThrows(RuntimeException.class, () ->
                messageAggregationService.deleteMessage(messageId));
    }
    
    @Test
    @DisplayName("测试获取用户消息 - 多种状态")
    void testGetMessages_MultipleStatuses() {
        // Given
        Message readMessage = Message.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .messageType("NOTIFICATION")
                .title("已读消息")
                .status(Message.MessageStatus.READ)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Page<Message> page = new PageImpl<>(List.of(message, readMessage));
        when(messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                eq(userId), eq(tenantId), any()))
                .thenReturn(page);
        
        // When
        Page<MessageDTO> result = messageAggregationService.getMessages(userId, tenantId, 0, 20);
        
        // Then
        assertThat(result.getContent()).hasSize(2);
    }
    
    @Test
    @DisplayName("测试获取用户消息 - 异常处理")
    void testGetMessages_Exception() {
        // Given
        when(messageRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                eq(userId), eq(tenantId), any()))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () ->
                messageAggregationService.getMessages(userId, tenantId, 0, 20));
    }
}

