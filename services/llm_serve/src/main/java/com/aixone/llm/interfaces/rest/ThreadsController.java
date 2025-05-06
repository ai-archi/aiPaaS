package com.aixone.llm.interfaces.rest;

import com.aixone.llm.domain.models.entities.thread.Thread;
import com.aixone.llm.application.command.thread.ThreadCommand;
import com.aixone.llm.application.facade.AssistantFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/threads")
@RequiredArgsConstructor
public class ThreadsController {
    private final AssistantFacade assistantFacade;

    @PostMapping
    public Mono<Thread> create(@RequestParam String assistantId, @RequestBody ThreadCommand command) {
        return assistantFacade.createThread(assistantId, command);
    }

    @GetMapping
    public Flux<Thread> list(@RequestParam String assistantId) {
        return assistantFacade.listThreads(assistantId);
    }

    @GetMapping("/{threadId}")
    public Mono<Thread> get(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantFacade.getThread(assistantId, threadId);
    }

    @PutMapping("/{threadId}")
    public Mono<Thread> update(@RequestParam String assistantId, @PathVariable String threadId, @RequestBody ThreadCommand command) {
        return assistantFacade.updateThread(assistantId, threadId, command);
    }

    @DeleteMapping("/{threadId}")
    public Mono<Void> delete(@RequestParam String assistantId, @PathVariable String threadId) {
        return assistantFacade.deleteThread(assistantId, threadId);
    }
} 