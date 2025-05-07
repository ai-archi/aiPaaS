package com.aixone.llm.interfaces.rest;

import com.aixone.llm.domain.models.entities.thread.Thread;
import com.aixone.llm.domain.services.AssistantService;
import com.aixone.llm.application.command.thread.ThreadCommand;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/threads")
@RequiredArgsConstructor
public class ThreadsController {
    private final AssistantService assistantService;

    @PostMapping
    public Mono<Thread> create(@RequestParam String assistantId, @RequestBody ThreadCommand command) {
        Thread thread = new Thread();
        thread.setTitle(command.getTitle());
        thread.setStatus(command.getStatus());
        thread.setUserId(command.getUserId());
        thread.setCreatedAt(java.time.LocalDateTime.now());
        thread.setUpdatedAt(java.time.LocalDateTime.now());
        return assistantService.createThread(assistantId, thread);
    }

    @GetMapping
    public Flux<Thread> list(@RequestParam String assistantId) {
        return assistantService.listThreads(assistantId);
    }

    @GetMapping("/{threadId}")
    public Mono<Thread> get(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantService.getThread(assistantId, threadId);
    }

    @PutMapping("/{threadId}")
    public Mono<Thread> update(@RequestParam String assistantId, @PathVariable String threadId, @RequestBody ThreadCommand command) {
        Thread thread = new Thread();
        thread.setId(threadId);
        thread.setAssistantId(assistantId);
        thread.setTitle(command.getTitle());
        thread.setStatus(command.getStatus());
        thread.setUserId(command.getUserId());
        thread.setUpdatedAt(java.time.LocalDateTime.now());
        return assistantService.updateThread(assistantId, threadId, thread);
    }

    @DeleteMapping("/{threadId}")
    public Mono<Void> delete(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantService.deleteThread(assistantId, threadId);
    }
} 