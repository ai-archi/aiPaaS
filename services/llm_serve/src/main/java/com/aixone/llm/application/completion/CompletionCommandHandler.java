package com.aixone.llm.application.completion;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.services.CompletionService;

import reactor.core.publisher.Flux;

@Component
public class CompletionCommandHandler {
    
    private final CompletionService completionService;

    public CompletionCommandHandler(CompletionService completionService) {
        this.completionService = completionService;
    }

    public Flux<CompletionResponse> handle(CompletionCommand command) {
        CompletionRequest request = command.toCompletionRequest();
        return completionService.completion(request);
    }
} 