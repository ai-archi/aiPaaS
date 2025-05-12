package com.aixone.llm.interfaces.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.assistant.AssistantCommand;
import com.aixone.llm.domain.models.assistant.Assistant;
import com.aixone.llm.domain.services.AssistantService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/assistants")
@RequiredArgsConstructor
public class AssistantsController {
    private final AssistantService assistantService;

    @PostMapping
    public Mono<Assistant> create(@PathVariable String tenantId, @RequestBody AssistantCommand command) {
        return assistantService.createAssistant(command.toAssistant(tenantId));
    }

    @GetMapping
    public Flux<Assistant> list(@PathVariable String tenantId) {
        return assistantService.listAssistants(tenantId);
    }

    @GetMapping("/{assistantId}")
    public Mono<Assistant> get(@PathVariable String tenantId, @PathVariable String assistantId) {
        return assistantService.getAssistant(tenantId, assistantId);
    }

    @PutMapping("/{assistantId}")
    public Mono<Assistant> update(@PathVariable String tenantId, @PathVariable String assistantId, @RequestBody AssistantCommand command) {
        return assistantService.updateAssistant(tenantId, assistantId, command.toAssistant(tenantId));
    }

    @DeleteMapping("/{assistantId}")
    public Mono<Void> delete(@PathVariable String tenantId, @PathVariable String assistantId) {
        return assistantService.deleteAssistant(tenantId, assistantId);
    }
} 