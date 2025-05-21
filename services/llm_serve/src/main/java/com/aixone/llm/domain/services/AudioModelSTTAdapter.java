package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.model.UserModelKey;

import reactor.core.publisher.Flux;

public interface AudioModelSTTAdapter {

    /**
     * 启动实时语音转文本（STT）
     * @param request 语音转文本请求对象，包含音频流等信息
     * @param key 用户模型Key
     * @return 实时识别结果流
     */
    Flux<STTResponse> recognizeSTT(STTRequest request, UserModelKey key) throws Exception;
} 