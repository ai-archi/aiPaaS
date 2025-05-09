package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.repositories.model.ModelInvokeRepository;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.services.ModelInvokeService;
import com.aixone.llm.domain.services.ModelService;
import com.aixone.llm.domain.services.ModelAdapterFactory;
import com.aixone.llm.domain.services.ModelAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelInvokeServiceImpl implements ModelInvokeService {
    private final ModelInvokeRepository modelInvokeRepository;
    private final ModelService modelService;
    private final ModelAdapterFactory modelAdapterFactory;
    private static final int MONITORING_WINDOW_SECONDS = 60;

    @Override
    public Flux<ModelResponse> invoke(ModelRequest request) {
        String modelName = request.getModel();
        return modelService.getModelByName(modelName)
            .filter(model -> model.isActive())
            .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
            .flatMapMany(model -> {
                String providerName = model.getProviderName();
                ModelAdapter adapter = modelAdapterFactory.getAdapter(providerName);
                if (adapter == null) {
                    return Flux.error(new IllegalStateException("No adapter found for provider: " + providerName));
                }
                if (request.isStream()) {
                    // 流式返回
                    return adapter.streamInvoke(model, request)
                        .doOnComplete(() -> {
                            // 可在此保存最终响应记录
                        });
                } else {
                    // 非流式返回
                    return adapter.invoke(model, request)
                        .doOnSuccess(response -> {
                            // 可在此保存响应记录
                        })
                        .flux(); // 转为Flux
                }
            });
    }

    @Override
    public Mono<Long> getUsedTokens(String userId, String modelId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(5); // 最近5分钟的使用量
        return modelInvokeRepository.getUserTokenUsage(userId, modelId, start, now);
    }

    @Override
    public Mono<Boolean> checkModelAvailability(String modelId) {
        return modelService.getModel(modelId)
            .map(model -> model.isActive())
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Long> getModelLatency(String modelId) {
        return modelInvokeRepository.getRecentLatency(modelId, MONITORING_WINDOW_SECONDS);
    }

    @Override
    public Mono<Double> getModelErrorRate(String modelId) {
        return modelInvokeRepository.getRecentErrorRate(modelId, MONITORING_WINDOW_SECONDS);
    }
} 