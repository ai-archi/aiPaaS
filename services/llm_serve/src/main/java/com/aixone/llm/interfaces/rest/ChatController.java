package com.aixone.llm.interfaces.rest;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.chat.ChatCompletionCommand;
import com.aixone.llm.application.chat.ChatCompletionCommandHandler;
import com.aixone.llm.domain.models.chat.ChatResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/v1/{tenantId}/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCompletionCommandHandler chatCommandHandler;

    @PostMapping(value = "/completions", produces = {
        MediaType.APPLICATION_JSON_VALUE, 
        MediaType.TEXT_EVENT_STREAM_VALUE})
    public Publisher<ChatResponse> chatCompletions(@PathVariable("tenantId") String tenantId, @RequestBody ChatCompletionCommand command, ServerHttpResponse response) {
        command.setTenantId(tenantId);
        if (command.isStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
            return chatCommandHandler.handle(command);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return chatCommandHandler.handle(command).next();
        }
    }
} 