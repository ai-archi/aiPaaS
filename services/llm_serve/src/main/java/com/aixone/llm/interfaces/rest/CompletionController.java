package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.completion.CompletionCommandHandler;
import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CompletionController {
    
    private final CompletionCommandHandler completionCommandHandler;

    @PostMapping("/completions")
    public Mono<ModelResponse> completions(@RequestBody CompletionCommand command) {
        return completionCommandHandler.handle(command);
    }
} 