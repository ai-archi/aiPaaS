package com.aixone.llm.interfaces.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.message.MessageCommand;
import com.aixone.llm.domain.models.chat.Message;
import com.aixone.llm.domain.services.AssistantService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/messages")
@RequiredArgsConstructor
public class MessagesController {
    private final AssistantService assistantService;

    @PostMapping
    public Mono<Message> create(@RequestParam String assistantId, @RequestParam String threadId, @RequestBody MessageCommand command) {
        Message message = new Message();
        message.setRole(command.getRole());
        message.setContent(command.getContent());
        message.setCreatedAt(System.currentTimeMillis() / 1000);
        return assistantService.createMessage(assistantId, threadId, message);
    }

    @GetMapping
    public Flux<Message> list(@RequestParam String assistantId, @RequestParam String threadId) {
        return assistantService.listMessages(assistantId, threadId);
    }

    @GetMapping("/{messageId}")
    public Mono<Message> get(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId) {
        return assistantService.getMessage(assistantId, threadId, messageId);
    }

    @PutMapping("/{messageId}")
    public Mono<Message> update(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId, @RequestBody MessageCommand command) {
        Message message = new Message();
        message.setId(messageId);
        message.setRole(command.getRole());
        message.setContent(command.getContent());
        message.setCreatedAt(System.currentTimeMillis() / 1000);
        return assistantService.updateMessage(assistantId, threadId, messageId, message);
    }

    @DeleteMapping("/{messageId}")
    public Mono<Void> delete(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId) {
        return assistantService.deleteMessage(assistantId, threadId, messageId);
    }
} 