package com.aixone.llm.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import com.aixone.llm.application.command.embedding.EmbeddingCommandHandler;

@RestController
@RequestMapping("/v1/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingCommandHandler embeddingCommandHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> createEmbedding(@RequestBody Map<String, Object> request) {
        return embeddingCommandHandler.handle(request);
    }
} 