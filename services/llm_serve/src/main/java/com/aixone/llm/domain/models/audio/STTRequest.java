package com.aixone.llm.domain.models.audio;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * STTRequest 表示阿里云流式语音识别请求参数。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class STTRequest implements Serializable {
    /**
     * 模型名称，如 "gummy-realtime-v1"
     */
    private String model;

    /**
     * 识别参数
     */
    private Input input;
    /**
     * 是否开启流式识别
     */
    private boolean stream;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input implements Serializable {
        /**
         * 音频格式，如 "pcm"、"wav" 等
         */
        private String format;
        /**
         * 采样率，如 16000
         */
        private Integer sampleRate;
        /**
         * 源语言，如 "auto"、"zh"、"en" 等
         */
        private String sourceLanguage;
        /**
         * 是否开启实时翻译
         */
        private Boolean translationEnabled;
        /**
         * 目标翻译语言列表，如 ["en"]
         */
        private List<String> translationLanguages;
        
        private File audioFile;
        

    }
} 