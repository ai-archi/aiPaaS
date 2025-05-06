package com.aixone.llm.domain.services;

import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import reactor.core.publisher.Mono;

public interface CompletionService {
    Mono<ModelResponse> completion(CompletionCommand command);
} 