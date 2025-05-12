package com.aixone.llm.application.model;

import org.springframework.stereotype.Service;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.services.ModelService;

import lombok.RequiredArgsConstructor;
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
    
    public Flux<ModelConfig> handleListModelsByProvider(String providerName) {
        return modelService.listModels()
            .filter(model -> model.getProviderName().equals(providerName));
    }

    public Flux<ModelConfig> handleQuery(ModelQuery query) {
        return modelService.listModels()
            .filter(model -> {
                boolean match = true;
                if (query.getActive() != null) {
                    match = match && (model.isActive() == query.getActive());
                }
                if (query.getProviderName() != null) {
                    match = match && query.getProviderName().equals(model.getProviderName());
                }
                return match;
            });
    }
} 