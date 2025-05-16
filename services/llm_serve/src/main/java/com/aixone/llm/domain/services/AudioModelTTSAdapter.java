package com.aixone.llm.domain.services;

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
    Flux<TTSResponse> recognizeTTS(TTSRequest request, UserModelKey key);

  
} 