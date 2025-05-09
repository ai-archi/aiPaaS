package com.aixone.llm.domain.services.impl;

import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.services.CompletionService;
import com.aixone.llm.domain.services.ModelAdapterFactory;
import com.aixone.llm.domain.services.ModelAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.aixone.llm.domain.models.values.config.ModelRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {
    private final ModelAdapterFactory modelAdapterFactory;

    @Override
    public Mono<ModelResponse> completion(ModelRequest request) {
       
        return Mono.just(ModelResponse.builder()
                .id(java.util.UUID.randomUUID().toString())
                .object("text_completion")
                .created(java.time.Instant.now().getEpochSecond())
                .model(request.getModel())
                .build());
    }
} 