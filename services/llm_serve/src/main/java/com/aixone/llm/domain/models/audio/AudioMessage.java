package com.aixone.llm.domain.models.audio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AudioMessage 表示音频对话中的一条消息，仅保留最小必要字段。
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AudioMessage implements Serializable {
    /**
     * 消息角色，如 "user"、"assistant"、"system"。
     */
    private String role;

    /**
     * 消息的文本内容（ASR 结果或 TTS 输入）。
     */
    private String content;
} 