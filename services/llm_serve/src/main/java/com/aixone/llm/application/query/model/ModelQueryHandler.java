package com.aixone.llm.application.query.model;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.services.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModelQueryHandler {
    private final ModelService modelService;
    
    public Mono<ModelConfig> handleGetModel(String modelId) {
        return modelService.getModel(modelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Model not found: " + modelId)));
    }
    
    public Flux<ModelConfig> handleListModels() {
        return modelService.listModels();
    }
    
    public Flux<ModelConfig> handleListActiveModels() {
        return modelService.listModels()
            .filter(ModelConfig::isActive);
    }
    
    public Flux<ModelConfig> handleListModelsByProvider(String providerId) {
        return modelService.listModels()
            .filter(model -> model.getProviderInfo().getProviderId().equals(providerId));
    }
} 