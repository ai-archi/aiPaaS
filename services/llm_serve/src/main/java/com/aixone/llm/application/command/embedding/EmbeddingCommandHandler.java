package com.aixone.llm.application.command.embedding;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class EmbeddingCommandHandler {
    @Autowired
    private EmbeddingService embeddingService;

    public Mono<Map<String, Object>> handle(Map<String, Object> request) {
        String model = (String) request.getOrDefault("model", "default");
        String input = (String) request.getOrDefault("input", "");
        return embeddingService.generateEmbedding(model, input)
                .map(embedding -> Map.of(
                        "id", "embd-" + System.currentTimeMillis(),
                        "object", "embedding",
                        "data", List.of(Map.of(
                                "index", 0,
                                "embedding", embedding,
                                "object", "embedding"
                        ))
                ));
    }
} 