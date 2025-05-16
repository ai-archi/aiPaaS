package com.aixone.llm.domain.models.audio;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TTSRequest 仅用于REST/非实时接口，保持简洁。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TTSRequest implements Serializable {
    /**
     * 模型名称
     */
    private String model;
    /**
     * input参数，包含text、voice、language等
     */
    private Input input;
    /**
     * 音频格式，如wav、mp3等
     */
    private String audioFormat;
    /**
     * 是否为流式请求
     */
    private Boolean stream;
    /**
     * 用户模型Key
     */
    private String keyId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * parameters参数，包含audio_format、stream等（WebSocket扩展）
     */
    private Map<String, Object> parameters;
    /**
     * 资源扩展，如热词、定制资源等（WebSocket扩展）
     */
    private List<Map<String, Object>> resources;
    /**
     * meta/trace_id等协议扩展字段（WebSocket扩展）
     */
    private Map<String, Object> meta;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input implements Serializable {
        /**
         * TTS文本内容（对应input.text）
         */
        private String text;
        /**
         * TTS音色（对应input.voice）
         */
        private String voice;
        /**
         * 语种，如zh、en等
         */
        private String language;
    }
}
