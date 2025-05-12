package com.aixone.llm.application.embedding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.embedding.EmbeddingRequest;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;
import com.aixone.llm.domain.services.EmbeddingService;

import reactor.core.publisher.Mono;

@Component
public class EmbeddingCommandHandler {
    @Autowired
    private EmbeddingService embeddingService;

    public Mono<EmbeddingResponse> handle(EmbeddingCommand command) {
        EmbeddingRequest request = command.toEmbeddingRequest();
        return embeddingService.generateEmbedding(request);
    }
} 