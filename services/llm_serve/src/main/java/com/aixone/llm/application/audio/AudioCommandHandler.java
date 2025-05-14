package com.aixone.llm.application.audio;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.audio.AudioRequest;
import com.aixone.llm.domain.models.audio.AudioResponse;
import com.aixone.llm.domain.services.ModelInvokeService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class AudioCommandHandler {
    private final ModelInvokeService modelInvokeService;

    public Flux<AudioResponse> handleASR(AudioASRCommand command) {
        AudioRequest request = command.toAudioRequest();
        return modelInvokeService.invokeASR(request);
    }

    public Flux<AudioResponse> handleTTS(AudioTTSCommand command) {
        AudioRequest request = command.toAudioRequest();
        return modelInvokeService.invokeTTS(request);
    }
} 