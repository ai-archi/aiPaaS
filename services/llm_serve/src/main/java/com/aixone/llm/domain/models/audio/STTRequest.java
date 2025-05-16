package com.aixone.llm.domain.models.audio;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ASRRequest 表示语音转文本请求参数，精简为阿里云ASR接口所需字段。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class STTRequest implements Serializable {
    /**
     * 模型名称
     */
    private String model;
    /**
     * input参数，包含file_urls、audio_format、language等
     */
    private Input input;
    /**
     * 是否为流式请求
     */
    private boolean stream;
    /**
     * 用户模型Key
     */
    private String keyId;
    /**
     * 用户ID
     */
    private String userId;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input implements Serializable {
        /**
         * 语音文件公网URL列表（阿里云ASR要求file_urls）
         */
        private List<String> file_urls;
        /**
         * 语种，如zh、en等
         */
        private String language;
        /**
         * 音频格式，如wav、mp3等
         */
        private String audio_format;
    }
} 