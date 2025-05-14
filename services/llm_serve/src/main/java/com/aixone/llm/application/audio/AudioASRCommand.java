package com.aixone.llm.application.audio;

import com.aixone.llm.domain.models.audio.AudioRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ASR（语音转文本）命令
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AudioASRCommand extends AudioRequest {
    public AudioRequest toAudioRequest() {
        return this;
    }
} 