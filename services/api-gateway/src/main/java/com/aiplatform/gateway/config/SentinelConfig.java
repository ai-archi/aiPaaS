package com.aiplatform.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SentinelConfig {

    @PostConstruct
    public void init() {
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 429);
                map.put("message", "请求被限流，请稍后重试");
                return ServerResponse.status(429)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(map);
            }
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
} 