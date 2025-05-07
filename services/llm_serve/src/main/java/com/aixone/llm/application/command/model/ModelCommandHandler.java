package com.aixone.llm.application.command.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.services.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModelCommandHandler {
    private final ModelService modelService;
    
    public Mono<ModelConfig> handleCreateModel(CreateModelCommand command) {
        ModelConfig modelConfig = command.toModelConfig();
        return modelService.validateModel(modelConfig)
            .filter(valid -> valid)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid model configuration")))
            .then(modelService.createModel(modelConfig));
    }
    
    public Mono<ModelConfig> handleUpdateModel(UpdateModelCommand command) {
        ModelConfig modelConfig = command.toModelConfig();
        return modelService.validateModel(modelConfig)
            .filter(valid -> valid)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid model configuration")))
            .then(modelService.updateModel( modelConfig));
    }
    
    public Mono<Void> handleDeleteModel(String modelId) {
        return modelService.deleteModel(modelId);
    }
    
    public Mono<ModelConfig> handleActivateModel(String modelId) {
        return modelService.activateModel(modelId);
    }
    
    public Mono<ModelConfig> handleDeactivateModel(String modelId) {
        return modelService.deactivateModel(modelId);
    }
} 