package com.aixone.llm.infrastructure.adapter;

import com.aixone.llm.domain.services.ModelAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import com.aixone.llm.domain.models.values.config.ModelRequest;
import com.aixone.llm.domain.models.values.config.ModelResponse;
import java.time.Instant;
import java.util.Collections;

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
    public Mono<ModelResponse> invoke(ModelRequest request) {
        String modelName = request.getModel();
        String prompt = request.getPrompt();
        Integer maxTokens = request.getMaxTokens();
        Double temperature = request.getTemperature();
        Double topP = request.getTopP();
        Boolean stream = request.getStream();
        // 构造请求体
        String body = "{" +
                "\"model\": \"" + modelName + "\"," +
                "\"messages\": [" +
                "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                "{\"role\": \"user\", \"content\": \"" + prompt + "\"}]" +
                (maxTokens != null ? ",\"max_tokens\": " + maxTokens : "") +
                (temperature != null ? ",\"temperature\": " + temperature : "") +
                (topP != null ? ",\"top_p\": " + topP : "") +
                (stream != null ? ",\"stream\": " + stream : "") +
                "}";
        return webClient.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    // 这里只做简单解析，实际应用中建议用Jackson/Gson解析
                    // 提取content和usage
                    String content = prompt + " (mock response)";
                    ModelResponse.Usage usage = ModelResponse.Usage.builder()
                            .promptTokens(10)
                            .completionTokens(10)
                            .totalTokens(20)
                            .build();
                    ModelResponse.Message message = ModelResponse.Message.builder()
                            .role("assistant")
                            .content(content)
                            .build();
                    ModelResponse.Choice choice = ModelResponse.Choice.builder()
                            .index(0)
                            .message(message)
                            .finishReason("stop")
                            .build();
                    return ModelResponse.builder()
                            .id("mock-id")
                            .object("chat.completion")
                            .created(Instant.now().getEpochSecond())
                            .model(modelName)
                            .choices(Collections.singletonList(choice))
                            .usage(usage)
                            .build();
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
}