package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {
    @Override
    public Mono<List<Float>> generateEmbedding(String model, String input) {
        // TODO: 实现真实的embedding生成逻辑
        return Mono.just(List.of(0.1f, 0.2f, 0.3f));
    }
} 