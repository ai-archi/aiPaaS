package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModelInvokeService {
    // 统一调用模型，支持流式和非流式
    Flux<ChatResponse> invokeChat(ChatRequest request);

    // Completion 能力
    Flux<CompletionResponse> invokeCompletion(CompletionRequest request);


    
    // 获取已使用的Token数量
    Mono<Long> getUsedTokens(String userId, String modelId);
    
    // 获取模型的实时状态
    Mono<Boolean> checkModelAvailability(String modelId);
    
    // 获取模型的延迟
    Mono<Long> getModelLatency(String modelId);
    
    // 获取模型的错误率
    Mono<Double> getModelErrorRate(String modelId);
} 