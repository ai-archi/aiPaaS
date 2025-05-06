package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {
    @Override
    public Mono<List<Float>> generateEmbedding(String model, String input) {
        // 返回固定的embedding向量
        return Mono.just(List.of(0.11f, 0.22f, 0.33f, 0.44f));
    }
} 