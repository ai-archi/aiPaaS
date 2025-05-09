package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    /**
     * 聊天补全接口，响应式返回
     */
    Flux<ModelResponse> chatCompletion(ModelRequest request);
} 