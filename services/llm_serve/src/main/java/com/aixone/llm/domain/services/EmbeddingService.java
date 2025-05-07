package com.aixone.llm.domain.services;

import reactor.core.publisher.Mono;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;

public interface EmbeddingService {
    Mono<ModelResponse> generateEmbedding(ModelRequest request);
} 