package com.aixone.llm.domain.services;

public interface ModelAdapterFactory {
    /**
     * 根据厂商名获取对应的模型适配器
     * @param providerName 厂商名（如 deepseek、openai 等）
     * @return 对应的ModelAdapter实例
     */
    ModelAdapter getAdapter(String providerName);

    /**
     * 根据厂商名获取对应的 WebSocket 语音识别适配器
     * @param providerName 厂商名（如 aliyun、baidu、tencent 等）
     * @return 对应的 SpeechRecognitionWebSocketAdapter 实例
     */
    AudioModelTTSAdapter getSpeechRecognitionWebSocketAdapter(String providerName);

  
} 