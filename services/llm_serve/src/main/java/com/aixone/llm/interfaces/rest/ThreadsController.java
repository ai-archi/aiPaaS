package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.thread.ThreadCommand;
import com.aixone.llm.domain.services.AssistantService;
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