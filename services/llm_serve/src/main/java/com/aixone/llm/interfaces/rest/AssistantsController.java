package com.aixone.llm.interfaces.rest;

import com.aixone.llm.domain.models.aggregates.assistant.Assistant;
import com.aixone.llm.application.command.assistant.AssistantCommand;
import com.aixone.llm.application.facade.AssistantFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/assistants")
@RequiredArgsConstructor
public class AssistantsController {
    private final AssistantFacade assistantFacade;

    @PostMapping
    public Mono<Assistant> create(@RequestBody AssistantCommand command) {
        return assistantFacade.createAssistant(command);
    }

    @GetMapping
    public Flux<Assistant> list() {
        return assistantFacade.listAssistants();
    }

    @GetMapping("/{assistantId}")
    public Mono<Assistant> get(@PathVariable String assistantId) {
        return assistantFacade.getAssistant(assistantId);
    }

    @PutMapping("/{assistantId}")
    public Mono<Assistant> update(@PathVariable String assistantId, @RequestBody AssistantCommand command) {
        return assistantFacade.updateAssistant(assistantId, command);
    }

    @DeleteMapping("/{assistantId}")
    public Mono<Void> delete(@PathVariable String assistantId) {
        return assistantFacade.deleteAssistant(assistantId);
    }
} 