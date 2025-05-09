package com.aixone.llm.application.command.embedding;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import com.aixone.llm.domain.models.embedding.EmbeddingRequest;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;

@Component
public class EmbeddingCommandHandler {
    @Autowired
    private EmbeddingService embeddingService;

    public Mono<EmbeddingResponse> handle(EmbeddingCommand command) {
        EmbeddingRequest request = command.toEmbeddingRequest();
        return embeddingService.generateEmbedding(request);
    }
} 