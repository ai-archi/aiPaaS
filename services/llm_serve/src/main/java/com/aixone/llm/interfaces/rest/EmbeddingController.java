package com.aixone.llm.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


import com.aixone.llm.application.command.embedding.EmbeddingCommandHandler;
import com.aixone.llm.application.command.embedding.EmbeddingCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;

@RestController
@RequestMapping("/v1/{tenantId}/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingCommandHandler embeddingCommandHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ModelResponse> createEmbedding(@RequestBody EmbeddingCommand command) {
        return embeddingCommandHandler.handle(command);
    }
} 