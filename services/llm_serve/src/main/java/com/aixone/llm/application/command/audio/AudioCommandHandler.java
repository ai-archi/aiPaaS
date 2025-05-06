package com.aixone.llm.application.command.audio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.aixone.llm.domain.services.AudioService;

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