package com.aixone.llm.domain.services;

import reactor.core.publisher.Mono;
import java.util.List;

public interface EmbeddingService {
    Mono<List<Float>> generateEmbedding(String model, String input);
} 