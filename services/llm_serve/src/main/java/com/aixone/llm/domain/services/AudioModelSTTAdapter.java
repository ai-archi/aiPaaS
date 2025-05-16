package com.aixone.llm.domain.services;

import java.io.InputStream;
import java.util.function.Consumer;

import com.aixone.llm.domain.models.audio.TTSResponse;
import com.aixone.llm.domain.models.model.UserModelKey;

import reactor.core.publisher.Flux;

public interface AudioModelSTTAdapter {
    /**
     * 启动实时文本转语音（TTS）
     * @param ttsRequest TTSRequest请求对象
     * @param onResult 音频流结果回调（如Base64、URL、二进制等，具体实现自定义）
     * @param onError 错误回调
     */
    Flux<TTSResponse> recognizeSTT(InputStream audioStream, UserModelKey key, Consumer<String> onResult, Consumer<Throwable> onError) throws Exception;
} 