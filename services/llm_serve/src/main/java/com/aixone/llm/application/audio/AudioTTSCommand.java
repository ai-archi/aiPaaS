package com.aixone.llm.application.audio;

import com.aixone.llm.domain.models.audio.AudioRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TTS（文本转语音）命令
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AudioTTSCommand extends AudioRequest {
    public AudioRequest toAudioRequest() {
        return this;
    }
} 