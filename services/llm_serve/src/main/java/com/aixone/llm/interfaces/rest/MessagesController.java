package com.aixone.llm.interfaces.rest;

import com.aixone.llm.domain.models.entities.message.Message;
import com.aixone.llm.application.command.message.MessageCommand;
import com.aixone.llm.application.facade.AssistantFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
public class MessagesController {
    private final AssistantFacade assistantFacade;

    @PostMapping
    public Mono<Message> create(@RequestParam String assistantId, @RequestParam String threadId, @RequestBody MessageCommand command) {
        return assistantFacade.createMessage(assistantId, threadId, command);
    }

    @GetMapping
    public Flux<Message> list(@RequestParam String assistantId, @RequestParam String threadId) {
        return assistantFacade.listMessages(assistantId, threadId);
    }

    @GetMapping("/{messageId}")
    public Mono<Message> get(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId) {
        return assistantFacade.getMessage(assistantId, threadId, messageId);
    }

    @PutMapping("/{messageId}")
    public Mono<Message> update(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId, @RequestBody MessageCommand command) {
        return assistantFacade.updateMessage(assistantId, threadId, messageId, command);
    }

    @DeleteMapping("/{messageId}")
    public Mono<Void> delete(@RequestParam String assistantId, @RequestParam String threadId, @PathVariable String messageId) {
        return assistantFacade.deleteMessage(assistantId, threadId, messageId);
    }
} 