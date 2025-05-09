package com.aixone.llm.infrastructure.adapter;

import com.aixone.llm.domain.services.ModelAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.http.MediaType;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;


@Component
public class DeepseekModelAdapter implements ModelAdapter, ModelAdapterFactoryImpl.ProviderNamed {
    private final WebClient webClient;

    public DeepseekModelAdapter(WebClient.Builder webClientBuilder) {
        // Deepseek API base url 可通过配置中心注入
        this.webClient = webClientBuilder.baseUrl("https://api.deepseek.com/v1").build();
    }

    @Override
    public String getProviderName() {
        return "deepseek";
    }

    @Override
    public Mono<ModelResponse> invoke(ModelConfig model,ModelRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + model.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModelResponse.class);
    }
    @Override
    public Flux<ModelResponse> streamInvoke(ModelConfig model, ModelRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + model.getApiKey())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(raw -> System.out.println("[Deepseek stream raw chunk]: " + raw))
                .filter(raw -> raw != null && !raw.trim().isEmpty() && !raw.trim().equals("[DONE]"))
                .map(raw -> {
                    try {
                        var mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().build();
                        String trimmed = raw.trim();
                        if (trimmed.startsWith("[")) {
                            java.util.List<ModelResponse> list = mapper.readValue(trimmed, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<ModelResponse>>() {});
                            return list.isEmpty() ? null : list.get(0);
                        } else {
                            return mapper.readValue(trimmed, ModelResponse.class);
                        }
                    } catch (Exception e) {
                        System.err.println("[Deepseek stream parse error]: " + e.getMessage());
                        return null;
                    }
                })
                .filter(resp -> resp != null);
    }
    @Override
    public Mono<Long> getQuota(String modelName) {
        // 模拟返回剩余额度
        return Mono.just(10000L);
    }

    @Override
    public Mono<Long> getUsage(String modelName) {
        // 模拟返回已用量
        return Mono.just(1234L);
    }

    @Override
    public Mono<Boolean> checkAvailability(String modelName) {
        // 模拟始终可用
        return Mono.just(true);
    }


}