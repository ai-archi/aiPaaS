package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.model.ModelCommandHandler;
import com.aixone.llm.application.command.model.CreateModelCommand;
import com.aixone.llm.application.command.model.UpdateModelCommand;
import com.aixone.llm.application.query.model.ModelQueryHandler;
import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelCommandHandler commandHandler;
    private final ModelQueryHandler queryHandler;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ModelConfig> createModel(@RequestBody CreateModelCommand command) {
        return commandHandler.handleCreateModel(command);
    }
    
    @PutMapping("/{modelId}")
    public Mono<ModelConfig> updateModel(@PathVariable String modelId, @RequestBody UpdateModelCommand command) {
        return commandHandler.handleUpdateModel(modelId, command);
    }
    
    @DeleteMapping("/{modelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteModel(@PathVariable String modelId) {
        return commandHandler.handleDeleteModel(modelId);
    }
    
    @GetMapping("/{modelId}")
    public Mono<ModelConfig> getModel(@PathVariable String modelId) {
        return queryHandler.handleGetModel(modelId);
    }
    
    @GetMapping
    public Flux<ModelConfig> listModels(@RequestParam(required = false) Boolean active,
                                      @RequestParam(required = false) String providerId) {
        if (active != null && active) {
            return queryHandler.handleListActiveModels();
        } else if (providerId != null) {
            return queryHandler.handleListModelsByProvider(providerId);
        }
        return queryHandler.handleListModels();
    }
    
    @PostMapping("/{modelId}/activate")
    public Mono<ModelConfig> activateModel(@PathVariable String modelId) {
        return commandHandler.handleActivateModel(modelId);
    }
    
    @PostMapping("/{modelId}/deactivate")
    public Mono<ModelConfig> deactivateModel(@PathVariable String modelId) {
        return commandHandler.handleDeactivateModel(modelId);
    }
} 