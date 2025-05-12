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

import com.aixone.llm.application.thread.ThreadCommand;
import com.aixone.llm.domain.services.AssistantService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/threads")
@RequiredArgsConstructor
public class ThreadsController {
    private final AssistantService assistantService;

    @PostMapping
    public Mono<ThreadCommand> create(@RequestParam String assistantId, @RequestBody ThreadCommand command) {
        return assistantService.createThread(assistantId, command);
    }

    @GetMapping
    public Flux<ThreadCommand> list(@RequestParam String assistantId) {
        return assistantService.listThreads(assistantId);
    }

    @GetMapping("/{threadId}")
    public Mono<ThreadCommand> get(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantService.getThread(assistantId, threadId);
    }

    @PutMapping("/{threadId}")
    public Mono<ThreadCommand> update(@RequestParam String assistantId, @PathVariable String threadId, @RequestBody ThreadCommand command) {
        return assistantService.updateThread(assistantId, threadId, command);
    }

    @DeleteMapping("/{threadId}")
    public Mono<Void> delete(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantService.deleteThread(assistantId, threadId);
    }
} 