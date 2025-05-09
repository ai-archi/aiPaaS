package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    /**
     * 聊天补全接口，响应式返回
     */
    Flux<ChatResponse> chatCompletion(ChatRequest request);
} 