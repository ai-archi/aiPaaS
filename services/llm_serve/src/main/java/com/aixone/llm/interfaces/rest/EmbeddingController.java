package com.aixone.llm.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.embedding.EmbeddingCommand;
import com.aixone.llm.application.embedding.EmbeddingCommandHandler;
import com.aixone.llm.domain.models.embedding.EmbeddingResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingCommandHandler embeddingCommandHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EmbeddingResponse> createEmbedding(@RequestBody EmbeddingCommand command) {
        return embeddingCommandHandler.handle(command);
    }
} 