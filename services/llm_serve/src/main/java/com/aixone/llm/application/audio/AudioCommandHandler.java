package com.aixone.llm.application.audio;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.services.AudioService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AudioCommandHandler {
    private final AudioService audioService;

    public Mono<String> handleTranscription(AudioTranscriptionCommand command) {
        return audioService.transcribe(command.getFile());
    }

    public Mono<String> handleTranslation(AudioTranslationCommand command) {
        return audioService.translate(command.getFile());
    }
} 