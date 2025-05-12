package com.aixone.llm.interfaces.rest;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.completion.CompletionCommand;
import com.aixone.llm.application.completion.CompletionCommandHandler;
import com.aixone.llm.domain.models.completion.CompletionResponse;

import lombok.RequiredArgsConstructor;

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