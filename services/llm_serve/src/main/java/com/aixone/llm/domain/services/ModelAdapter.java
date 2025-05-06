package com.aixone.llm.domain.services;

import reactor.core.publisher.Mono;

public interface ModelAdapter {
    /**
     * 调用大模型，参数和返回值可根据实际业务扩展
     * @param prompt 输入提示
     * @param modelName 模型名称
     * @return 模型返回结果
     */
    Mono<String> invoke(String prompt, String modelName);
} 