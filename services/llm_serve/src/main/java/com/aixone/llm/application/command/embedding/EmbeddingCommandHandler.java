package com.aixone.llm.application.command.embedding;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;

@Component
public class EmbeddingCommandHandler {
    @Autowired
    private EmbeddingService embeddingService;

    public Mono<ModelResponse> handle(EmbeddingCommand command) {
        ModelRequest request = command.toModelRequest();
        return embeddingService.generateEmbedding(request);
    }
} 