package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.values.config.ModelResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ModelInvokeService {
    // 同步调用模型
    Mono<ModelResponse> invoke(String userId, String modelId, String prompt,
                             List<Map<String, String>> messages,
                             Map<String, Object> parameters);
    
    // 流式调用模型
    Flux<ModelResponse> streamInvoke(String userId, String modelId, String prompt,
                                   List<Map<String, String>> messages,
                                   Map<String, Object> parameters);
    
    // 获取已使用的Token数量
    Mono<Long> getUsedTokens(String userId, String modelId);
    
    // 获取模型的实时状态
    Mono<Boolean> checkModelAvailability(String modelId);
    
    // 获取模型的延迟
    Mono<Long> getModelLatency(String modelId);
    
    // 获取模型的错误率
    Mono<Double> getModelErrorRate(String modelId);
} 