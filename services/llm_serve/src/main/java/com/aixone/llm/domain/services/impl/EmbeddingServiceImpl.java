package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.EmbeddingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    @Override
    public Mono<ModelResponse> generateEmbedding(ModelRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateEmbedding'");
    }
} 