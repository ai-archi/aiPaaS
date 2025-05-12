package com.aixone.llm.infrastructure.adapter;

import com.aixone.llm.domain.services.ModelAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.http.MediaType;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import java.io.IOException;


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
    public Mono<ChatResponse> invokeChat(ModelConfig model, ChatRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + model.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class);
    }
    @Override
    public Flux<ChatResponse> invokeChatStream(ModelConfig model, ChatRequest request) {
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
                        var mapper = JsonMapper.builder().build();
                        String trimmed = raw.trim();
                        if (trimmed.startsWith("[")) {
                            java.util.List<ChatResponse> list = mapper.readValue(trimmed, new TypeReference<java.util.List<ChatResponse>>() {});
                            return list.isEmpty() ? null : list.get(0);
                        } else {
                            return mapper.readValue(trimmed, ChatResponse.class);
                        }
                    } catch (IOException e) {
                        System.err.println("[Deepseek stream parse error]: " + e.getMessage());
                        return null;
                    }
                })
                .filter(resp -> resp != null);
    }

    @Override
    public Mono<CompletionResponse> invokeCompletion(ModelConfig model, CompletionRequest request) {
        WebClient webClientBeta = WebClient.builder().baseUrl("https://api.deepseek.com/beta").build();
        return webClientBeta.post()
        .uri("/completions")
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + model.getApiKey())
        .bodyValue(request)
        .retrieve()
        .bodyToMono(CompletionResponse.class);
    }

    @Override
    public Flux<CompletionResponse> invokeCompletionStream(ModelConfig model, CompletionRequest request) {
        WebClient webClientBeta = WebClient.builder().baseUrl("https://api.deepseek.com/beta").build();
        return webClientBeta.post()
                .uri("/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + model.getApiKey())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(raw -> System.out.println("[Deepseek completions stream raw chunk]: " + raw))
                .filter(raw -> raw != null && !raw.trim().isEmpty() && !raw.trim().equals("[DONE]"))
                .map(raw -> {
                    try {
                        var mapper = JsonMapper.builder().build();
                        String trimmed = raw.trim();
                        if (trimmed.startsWith("[")) {
                            java.util.List<CompletionResponse> list = mapper.readValue(trimmed, new TypeReference<java.util.List<CompletionResponse>>() {});
                            if (list.isEmpty()) throw new RuntimeException("Empty CompletionResponse list");
                            return list.get(0);
                        } else {
                            return mapper.readValue(trimmed, CompletionResponse.class);
                        }
                    } catch (IOException e) {
                        System.err.println("[Deepseek completions stream parse error]: " + e.getMessage());
                        throw new RuntimeException("[Deepseek completions stream parse error]", e);
                    }
                });
    }
    @Override
    public Mono<ImageResponse> submitImageTask(ModelConfig model, ImageRequest request) {
        // 不支持图片任务
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
    }

    @Override
    public Mono<ImageResponse> getImageTaskResult(ModelConfig model, String taskId) {
        // 不支持图片任务
        return Mono.error(new UnsupportedOperationException("Not implemented yet"));
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