package com.aixone.llm.application.audio;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.audio.ASRRequest;
import com.aixone.llm.domain.models.audio.AudioResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.services.ModelInvokeService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class AudioCommandHandler {
    private final ModelInvokeService modelInvokeService;

    public Flux<AudioResponse> handleASR(ASRRequest request) {
        return modelInvokeService.invokeASR(request);
    }

    public Flux<AudioResponse> handleTTS(TTSRequest request) {
        return modelInvokeService.invokeTTS(request);
    }
} 