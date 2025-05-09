package com.aixone.llm.domain.services;

import reactor.core.publisher.Mono;
import com.aixone.llm.domain.models.embedding.EmbeddingRequest;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;

public interface EmbeddingService {
    Mono<EmbeddingResponse> generateEmbedding(EmbeddingRequest request);
} 