package com.aixone.llm.domain.services;

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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModelAdapter {
    /**
     * 调用大模型，参数和返回值均为领域模型（chat 能力）
     */
    Mono<ChatResponse> invokeChat(ModelConfig model, ChatRequest request, UserModelKey key);
    /**
     * 流式调用大模型（chat 能力）
     */
    Flux<ChatResponse> invokeChatStream(ModelConfig model, ChatRequest request, UserModelKey key);

    /**
     * 调用大模型的 completions 能力
     */
    Mono<CompletionResponse> invokeCompletion(ModelConfig model, CompletionRequest request, UserModelKey key);
    /**
     * 流式调用大模型的 completions 能力（如有需要）
     */
    Flux<CompletionResponse> invokeCompletionStream(ModelConfig model, CompletionRequest request, UserModelKey key);

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
     * 创建图片（生成）
     */
    Mono<ImageResponse> generateImage(ModelConfig model, ImageRequest request, UserModelKey key);
    /**
     * 编辑图片
     */
    Mono<ImageResponse> editImage(ModelConfig model, ImageRequest request, UserModelKey key);
    /**
     * 生成图片变体
     */
    Mono<ImageResponse> variationImage(ModelConfig model, ImageRequest request, UserModelKey key);

    /**
     * 根据任务ID获取图片处理结果。
     * @param model 模型配置
     * @param taskId 任务ID
     * @param key 用户模型Key
     * @return 图片处理结果
     */
    Mono<ImageTaskResponse> getImageTaskResult(ModelConfig model, String taskId, UserModelKey key);

    /**
     * 调用大模型的语音转文本（ASR）能力
     */
    Mono<AudioResponse> invokeASR(ModelConfig model, AudioRequest request, UserModelKey key);
    /**
     * 调用大模型的文本转语音（TTS）能力
     */
    Mono<AudioResponse> invokeTTS(ModelConfig model, AudioRequest request, UserModelKey key);
    /**
     * 流式调用大模型的语音转文本（ASR）能力
     */
    Flux<AudioResponse> invokeASRStream(ModelConfig model, AudioRequest request, UserModelKey key);
    /**
     * 流式调用大模型的文本转语音（TTS）能力
     */
    Flux<AudioResponse> invokeTTSStream(ModelConfig model, AudioRequest request, UserModelKey key);
} 