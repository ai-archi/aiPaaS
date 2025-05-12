package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.aggregates.model_config.ModelConfig;
import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModelAdapter {
    /**
     * 调用大模型，参数和返回值均为领域模型（chat 能力）
     */
    Mono<ChatResponse> invokeChat(ModelConfig model, ChatRequest request);
    /**
     * 流式调用大模型（chat 能力）
     */
    Flux<ChatResponse> invokeChatStream(ModelConfig model, ChatRequest request);

    /**
     * 调用大模型的 completions 能力
     */
    Mono<CompletionResponse> invokeCompletion(ModelConfig model, CompletionRequest request);
    /**
     * 流式调用大模型的 completions 能力（如有需要）
     */
    Flux<CompletionResponse> invokeCompletionStream(ModelConfig model, CompletionRequest request);

    /**
     * 查询模型配额
     * @param modelName 模型名称
     * @return 剩余额度
     */
    Mono<Long> getQuota(String modelName);

    /**
     * 查询模型用量
     * @param modelName 模型名称
     * @return 已用量
     */
    Mono<Long> getUsage(String modelName);

    /**
     * 检查模型可用性
     * @param modelName 模型名称
     * @return 是否可用
     */
    Mono<Boolean> checkAvailability(String modelName);

    /**
     * 发起图片生成/编辑异步请求，返回任务ID等初步信息。
     */
    Mono<ImageResponse> submitImageTask(ModelConfig model, ImageRequest request);

    /**
     * 根据任务ID获取图片处理结果。
     * @param model 模型配置
     * @param taskId 任务ID
     * @return 图片处理结果
     */
    Mono<ImageResponse> getImageTaskResult(ModelConfig model, String taskId);
} 