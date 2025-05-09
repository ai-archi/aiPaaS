package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.embedding.EmbeddingRequest;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;
import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    @Override
    public Mono<EmbeddingResponse> generateEmbedding(EmbeddingRequest request) {
        // TODO: 实现向量生成逻辑
        throw new UnsupportedOperationException("Unimplemented method 'generateEmbedding'");
    }
} 