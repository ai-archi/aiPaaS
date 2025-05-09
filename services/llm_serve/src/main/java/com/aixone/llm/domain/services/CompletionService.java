package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import reactor.core.publisher.Flux;

public interface CompletionService {
    Flux<CompletionResponse> completion(CompletionRequest request);
} 