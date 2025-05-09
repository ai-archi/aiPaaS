package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.completion.CompletionCommandHandler;
import com.aixone.llm.application.command.completion.CompletionCommand;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.reactivestreams.Publisher;

@RestController
@RequestMapping("/v1/{tenantId}")
@RequiredArgsConstructor
public class CompletionController {
    
    private final CompletionCommandHandler completionCommandHandler;

    @PostMapping("/completions")
    public Publisher<CompletionResponse> completions(@PathVariable("tenantId") String tenantId, @RequestBody CompletionCommand command, ServerHttpResponse response) {
        command.setTenantId(tenantId);
        if (command.isStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
            return completionCommandHandler.handle(command);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return completionCommandHandler.handle(command).next()  ;
        }
    }
} 