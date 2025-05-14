package com.aixone.llm.domain.models.audio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AudioRequest 表示音频请求参数，精简为阿里云TTS/ASR接口所需字段。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AudioRequest implements Serializable {
    /**
     * 请求类型："asr" 或 "tts"
     */
    private String type;

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
     * ASR音频内容，Base64字符串
     */
    private String audio;

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