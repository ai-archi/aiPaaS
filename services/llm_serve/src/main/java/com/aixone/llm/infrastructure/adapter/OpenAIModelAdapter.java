package com.aixone.llm.infrastructure.adapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.aixone.llm.domain.models.audio.AudioRequest;
import com.aixone.llm.domain.models.audio.AudioResponse;
import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;
import com.aixone.llm.domain.models.model.ModelConfig;
import com.aixone.llm.domain.models.model.UserModelKey;
import com.aixone.llm.domain.services.ModelAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public class OpenAIModelAdapter implements ModelAdapter, ModelAdapterFactoryImpl.ModelNamed {
    protected final WebClient webClient;

    public OpenAIModelAdapter(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public OpenAIModelAdapter(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://api.openai.com/v1");
    }

    @Override
    public List<String> getModelNames() {
        return Arrays.asList("gpt-4o","gpt-4o-mini");
    }

    @Override
    public Mono<ChatResponse> invokeChat(ModelConfig model, ChatRequest request, UserModelKey key) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class);
    }
    @Override
    public Flux<ChatResponse> invokeChatStream(ModelConfig model, ChatRequest request, UserModelKey key) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key.getApiKey())
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
    public Mono<CompletionResponse> invokeCompletion(ModelConfig model, CompletionRequest request, UserModelKey key) {
        WebClient webClientBeta = WebClient.builder().baseUrl("https://api.deepseek.com/beta").build();
        return webClientBeta.post()
        .uri("/completions")
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + key.getApiKey())
        .bodyValue(request)
        .retrieve()
        .bodyToMono(CompletionResponse.class);
    }

    @Override
    public Flux<CompletionResponse> invokeCompletionStream(ModelConfig model, CompletionRequest request, UserModelKey key) {
        WebClient webClientBeta = WebClient.builder().baseUrl("https://api.deepseek.com/beta").build();
        return webClientBeta.post()
                .uri("/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key.getApiKey())
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

    @Override
    public Mono<ImageResponse> generateImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Deepseek暂不支持图片生成");
    }

    @Override
    public Mono<ImageResponse> editImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Deepseek暂不支持图片编辑");
    }

    @Override
    public Mono<ImageResponse> variationImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Deepseek暂不支持图片变体");
    }

    @Override
    public Mono<ImageTaskResponse> getImageTaskResult(ModelConfig model, String taskId, UserModelKey key) {
        throw new UnsupportedOperationException("Deepseek暂不支持图片任务查询");
    }


    @Override
    public Mono<AudioResponse> invokeASR(ModelConfig model, AudioRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Unimplemented method 'invokeASR'");
    }

    @Override
    public Mono<AudioResponse> invokeTTS(ModelConfig model, AudioRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Unimplemented method 'invokeTTS'");
    }

    @Override
    public Flux<AudioResponse> invokeASRStream(ModelConfig model, AudioRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Unimplemented method 'invokeASRStream'");
    }

    @Override
    public Flux<AudioResponse> invokeTTSStream(ModelConfig model, AudioRequest request, UserModelKey key) {
        throw new UnsupportedOperationException("Unimplemented method 'invokeTTSStream'");
    }

}