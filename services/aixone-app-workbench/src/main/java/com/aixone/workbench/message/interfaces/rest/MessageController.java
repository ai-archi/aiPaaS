package com.aixone.workbench.message.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.workbench.message.application.dto.MessageDTO;
import com.aixone.workbench.message.domain.service.MessageAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 消息控制器
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/workbench/messages")
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageAggregationService messageAggregationService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getMessages(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("获取用户消息: userId={}, tenantId={}", userId, tenantId);
        
        Page<MessageDTO> messages = messageAggregationService.getMessages(userId, tenantId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID messageId) {
        log.info("标记消息为已读: messageId={}", messageId);
        
        messageAggregationService.markAsRead(messageId);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable UUID messageId) {
        log.info("删除消息: messageId={}", messageId);
        
        messageAggregationService.deleteMessage(messageId);
        
        return ResponseEntity.ok(ApiResponse.success());
    }
}

