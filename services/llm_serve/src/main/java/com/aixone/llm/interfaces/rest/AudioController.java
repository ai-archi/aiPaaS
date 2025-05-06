package com.aixone.llm.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/audio")
@RequiredArgsConstructor
public class AudioController {
    private final com.aixone.llm.application.command.audio.AudioCommandHandler audioCommandHandler;

    /**
     * 音频转写接口
     * 支持SSE和普通模式，SSE模式下返回实时转写结果
     */
    @PostMapping(value = "/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> transcribeAudio(@RequestPart("file") MultipartFile file) {
        return audioCommandHandler.handleTranscription(new com.aixone.llm.application.command.audio.AudioTranscriptionCommand(file));
    }

    /**
     * 音频翻译接口
     * 支持SSE和普通模式，SSE模式下返回实时翻译结果
     */
    @PostMapping(value = "/translations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> translateAudio(@RequestPart("file") MultipartFile file) {
        return audioCommandHandler.handleTranslation(new com.aixone.llm.application.command.audio.AudioTranslationCommand(file));
    }

    // 可扩展SSE接口，后续根据业务需求实现
    // @PostMapping(value = "/transcriptions/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // public Flux<ServerSentEvent<String>> transcribeAudioStream(@RequestPart("file") MultipartFile file) {
    //     // TODO: 实现SSE实时转写
    //     return Flux.empty();
    // }
} 