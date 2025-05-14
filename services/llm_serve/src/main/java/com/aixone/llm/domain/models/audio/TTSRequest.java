package com.aixone.llm.domain.models.audio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TTSRequest 表示文本转语音请求参数，精简为阿里云TTS接口所需字段。
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
    /**
     * 音频格式，如wav、mp3等
     */
    private String audioFormat;
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
} 