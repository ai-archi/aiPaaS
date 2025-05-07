package com.aixone.llm.interfaces.rest;

import com.aixone.llm.application.command.model.ModelCommandHandler;
import com.aixone.llm.application.command.model.CreateModelCommand;
import com.aixone.llm.application.command.model.UpdateModelCommand;
import com.aixone.llm.application.query.model.ModelQueryHandler;
import com.aixone.llm.application.query.model.ModelQuery;
import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/{tenantId}/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelCommandHandler commandHandler;
    private final ModelQueryHandler queryHandler;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ModelConfig> createModel(@PathVariable("tenantId") String tenantId, @RequestBody CreateModelCommand command) {
        command.setTenantId(tenantId);
        return commandHandler.handleCreateModel(command);
    }
    
    @PutMapping("/{modelId}")
    public Mono<ModelConfig> updateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId, @RequestBody UpdateModelCommand command) {
        command.setId(modelId);
        command.setTenantId(tenantId);
        return commandHandler.handleUpdateModel(command);
    }
    
    @DeleteMapping("/{modelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleDeleteModel(modelId);
    }
    
    @GetMapping("/{modelId}")
    public Mono<ModelConfig> getModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return queryHandler.handleGetModel(modelId);
    }
    
    @GetMapping
    public Flux<ModelConfig> listModels(@PathVariable("tenantId") String tenantId, @ModelAttribute ModelQuery query) {
        return queryHandler.handleQuery(query);
    }
    
    @PostMapping("/{modelId}/activate")
    public Mono<ModelConfig> activateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleActivateModel(modelId);
    }
    
    @PostMapping("/{modelId}/deactivate")
    public Mono<ModelConfig> deactivateModel(@PathVariable("tenantId") String tenantId, @PathVariable String modelId) {
        return commandHandler.handleDeactivateModel(modelId);
    }
} 