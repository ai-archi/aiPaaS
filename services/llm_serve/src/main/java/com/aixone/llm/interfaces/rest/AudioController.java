package com.aixone.llm.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.audio.AudioCommandHandler;
import com.aixone.llm.domain.models.audio.ASRRequest;
import com.aixone.llm.domain.models.audio.AudioResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/{tenantId}/audio")
@RequiredArgsConstructor
public class AudioController {
    private final AudioCommandHandler audioCommandHandler;

    /**
     * 文本转语音（TTS Text-to-Voice），支持流式和非流式，统一返回AudioResponse
     */
    @PostMapping(value = "/speech", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<AudioResponse> createSpeech(
        @PathVariable("tenantId") String tenantId,
        @RequestBody TTSRequest req,
        ServerHttpResponse response
    ) {
        req.setUserId(tenantId);
        if (req.isStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
        return audioCommandHandler.handleTTS(req);
    }

    /**
     * 语音转文本（ASR），支持流式和非流式，统一返回AudioResponse
     */
    @PostMapping(value = "/transcriptions", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<AudioResponse> createTranscription(
        @PathVariable("tenantId") String tenantId,
        @RequestBody ASRRequest req,
        ServerHttpResponse response
    ) {
        req.setUserId(tenantId);
        if (req.isStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
        return audioCommandHandler.handleASR(req);
    }
} 