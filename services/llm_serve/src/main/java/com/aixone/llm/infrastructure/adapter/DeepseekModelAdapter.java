package com.aixone.llm.infrastructure.adapter;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DeepseekModelAdapter extends OpenAIModelAdapter {
    public DeepseekModelAdapter(WebClient.Builder webClientBuilder) {
        super(webClientBuilder, "https://api.deepseek.com/v1");
    }

    @Override
    public List<String> getModelNames() {
        return Arrays.asList("deepseek-chat", "deepseek-reasoner");
    }
}