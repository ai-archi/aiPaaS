package com.aixone.llm.infrastructure.adapter;

import com.aixone.llm.domain.services.ModelAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

@Component
public class DeepseekModelAdapter implements ModelAdapter, ModelAdapterFactoryImpl.ProviderNamed {
    private final WebClient webClient;
    private final String apiKey;

    public DeepseekModelAdapter(WebClient.Builder webClientBuilder,
                                @Value("${llm.deepseek.api-key}") String apiKey) {
        // Deepseek API base url 可通过配置中心注入
        this.webClient = webClientBuilder.baseUrl("https://api.deepseek.com/v1").build();
        this.apiKey = apiKey;
    }

    @Override
    public String getProviderName() {
        return "deepseek";
    }

    @Override
    public Mono<String> invoke(String prompt, String modelName) {
        String body = "{" +
                "\"model\": \"" + modelName + "\"," +
                "\"messages\": [" +
                "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                "{\"role\": \"user\", \"content\": \"" + prompt + "\"}]" +
                ",\"stream\": false" +
                "}";
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}