package com.aixone.llm.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aixone.llm.application.audio.AudioCommandHandler;
import com.aixone.llm.application.audio.AudioTranscriptionCommand;
import com.aixone.llm.application.audio.AudioTranslationCommand;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/v1/{tenantId}/audio")
@RequiredArgsConstructor
public class AudioController {
    private final AudioCommandHandler audioCommandHandler;

    /**
     * 音频转写接口
     * 支持SSE和普通模式，SSE模式下返回实时转写结果
     */
    @PostMapping(value = "/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> transcribeAudio(@RequestPart("file") MultipartFile file) {
        return audioCommandHandler.handleTranscription(new AudioTranscriptionCommand(file));
    }

    /**
     * 音频翻译接口
     * 支持SSE和普通模式，SSE模式下返回实时翻译结果
     */
    @PostMapping(value = "/translations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> translateAudio(@RequestPart("file") MultipartFile file) {
        return audioCommandHandler.handleTranslation(new AudioTranslationCommand(file));
    }

    // 可扩展SSE接口，后续根据业务需求实现
    // @PostMapping(value = "/transcriptions/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    // public Flux<ServerSentEvent<String>> transcribeAudioStream(@RequestPart("file") MultipartFile file) {
    //     // TODO: 实现SSE实时转写
    //     return Flux.empty();
    // }
} 