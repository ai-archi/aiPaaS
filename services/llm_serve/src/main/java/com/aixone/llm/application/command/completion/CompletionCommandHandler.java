package com.aixone.llm.application.command.completion;

import com.aixone.llm.domain.services.CompletionService;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CompletionCommandHandler {
    
    private final CompletionService completionService;

    public CompletionCommandHandler(CompletionService completionService) {
        this.completionService = completionService;
    }

    public Mono<ModelResponse> handle(CompletionCommand command) {
        ModelRequest request = command.toModelRequest();
        return completionService.completion(request);
    }
} 