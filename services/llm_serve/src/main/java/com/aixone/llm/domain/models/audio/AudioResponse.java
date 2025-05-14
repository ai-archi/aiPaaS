package com.aixone.llm.domain.models.audio;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AudioResponse 精简版，仅保留核心字段，兼容阿里云TTS/ASR返回结构。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AudioResponse implements Serializable {
    /**
     * 响应类型："asr" 或 "tts"
     */
    private String type;

    /**
     * 消息列表（ASR为文本，TTS为Base64音频字符串）
     */
    private List<AudioMessage> messages;

    /**
     * 阿里云TTS/ASR原始output结构
     */
    private Output output;

    /**
     * 请求ID
     */
    private String requestId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output implements Serializable {
        private String finishReason;
        private Audio audio;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Audio implements Serializable {
        private Long expiresAt;
        private String data; // base64音频数据
        private String id;
        private String url; // 完整音频文件的URL
    }
} 