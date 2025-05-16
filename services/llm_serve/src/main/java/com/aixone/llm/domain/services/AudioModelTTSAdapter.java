package com.aixone.llm.domain.services;

import java.io.InputStream;
import java.util.function.Consumer;

import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
import com.aixone.llm.domain.models.model.UserModelKey;

import reactor.core.publisher.Flux;

public interface AudioModelTTSAdapter {
    /**
     * 启动实时语音识别
     * @param audioStream 音频流
     * @param apiKey 接口密钥
     * @param onResult 识别结果回调（返回JSON字符串或自定义结果对象）
     * @param onError 错误回调
     */
    void recognizeSTT( InputStream audioStream, UserModelKey key,Consumer<String> onResult, Consumer<Throwable> onError);

    /**
     * 启动实时文本转语音（TTS）
     * @param ttsRequest TTSRequest请求对象
     * @param onResult 音频流结果回调（如Base64、URL、二进制等，具体实现自定义）
     * @param onError 错误回调
     */
    Flux<TTSResponse> recognizeTTS(TTSRequest ttsRequest, UserModelKey key) throws Exception;
} 