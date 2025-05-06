package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.models.values.config.ModelResponse;
import com.aixone.llm.domain.repositories.assistant.ModelInvokeRepository;
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

    private ModelResponse createChunk(String content, String modelId) {
        ModelResponse.Message message = ModelResponse.Message.builder()
                .role("assistant")
                .content(content)
                .build();
        ModelResponse.Choice choice = ModelResponse.Choice.builder()
                .index(0)
                .message(message)
                .finishReason("stop")
                .build();
        ModelResponse.Usage usage = ModelResponse.Usage.builder()
                .promptTokens(5)
                .completionTokens(5)
                .totalTokens(10)
                .build();
        return ModelResponse.builder()
                .id(UUID.randomUUID().toString())
                .object("chat.completion.chunk")
                .created(Instant.now().getEpochSecond())
                .model(modelId)
                .choices(Collections.singletonList(choice))
                .usage(usage)
                .build();
    }

    @Override
    public Mono<ModelResponse> invoke(String userId, String modelId, String prompt,
                                      List<Map<String, String>> messages,
                                      Map<String, Object> parameters) {
        LocalDateTime invokeTime = LocalDateTime.now();
        return modelService.getModel(modelId)
            .filter(model -> model.isAvailable())
            .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
            .flatMap(model -> {
                // 通过模型配置获取providerName
                String providerName = model.getProviderName();
                ModelAdapter adapter = modelAdapterFactory.getAdapter(providerName);
                if (adapter == null) {
                    return Mono.error(new IllegalStateException("No adapter found for provider: " + providerName));
                }
                // 这里只传递prompt和modelName，实际可扩展为传递messages和parameters
                return adapter.invoke(prompt, model.getModelName())
                    .map(responseStr -> {
                        // 假设responseStr为json字符串，实际应解析为ModelResponse
                        // 这里只做简单封装，生产环境应做json解析
                        ModelResponse.Message message = ModelResponse.Message.builder()
                                .role("assistant")
                                .content(responseStr)
                                .build();
                        ModelResponse.Choice choice = ModelResponse.Choice.builder()
                                .index(0)
                                .message(message)
                                .finishReason("stop")
                                .build();
                        ModelResponse.Usage usage = ModelResponse.Usage.builder()
                                .promptTokens(50)
                                .completionTokens(50)
                                .totalTokens(100)
                                .build();
                        return ModelResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .object("chat.completion")
                                .created(Instant.now().getEpochSecond())
                                .model(modelId)
                                .choices(Collections.singletonList(choice))
                                .usage(usage)
                                .build();
                    });
            })
            .doOnSuccess(response ->
                modelInvokeRepository.saveInvokeRecord(userId, modelId, response, invokeTime)
                    .subscribe()
            );
    }

    @Override
    public Flux<ModelResponse> streamInvoke(String userId, String modelId, String prompt,
                                            List<Map<String, String>> messages,
                                            Map<String, Object> parameters) {
        LocalDateTime invokeTime = LocalDateTime.now();
        return modelService.getModel(modelId)
            .filter(model -> model.isAvailable())
            .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
            .flatMapMany(model -> {
                // TODO: 实现具体的流式调用逻辑
                return Flux.just(
                    createChunk("第一部分响应", modelId),
                    createChunk("第二部分响应", modelId),
                    createChunk("第三部分响应", modelId)
                );
            })
            .doOnComplete(() -> {
                ModelResponse.Message message = ModelResponse.Message.builder()
                        .role("assistant")
                        .content("最终响应")
                        .build();
                ModelResponse.Choice choice = ModelResponse.Choice.builder()
                        .index(0)
                        .message(message)
                        .finishReason("stop")
                        .build();
                ModelResponse.Usage usage = ModelResponse.Usage.builder()
                        .promptTokens(50)
                        .completionTokens(50)
                        .totalTokens(100)
                        .build();
                ModelResponse finalResponse = ModelResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .object("chat.completion")
                        .created(Instant.now().getEpochSecond())
                        .model(modelId)
                        .choices(Collections.singletonList(choice))
                        .usage(usage)
                        .build();
                modelInvokeRepository.saveInvokeRecord(userId, modelId, finalResponse, invokeTime)
                    .subscribe();
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
            .map(model -> model.isAvailable())
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