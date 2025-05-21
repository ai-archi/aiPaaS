package com.aixone.llm.interfaces.rest;

import java.io.File;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.llm.application.audio.AudioCommandHandler;
import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/{tenantId}/audio")
@RequiredArgsConstructor
public class AudioController {
    private final AudioCommandHandler audioCommandHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 文本转语音（TTS Text-to-Voice），支持流式和非流式，统一返回TTSResponse
     */
    @PostMapping(value = "/tts", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<TTSResponse> createSpeech(
        @PathVariable("tenantId") String tenantId,
        @RequestBody TTSRequest req,
        ServerHttpResponse response
    ) {
        req.setUserId(tenantId);
        if (req.getStream() != null && req.getStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
        return audioCommandHandler.handleTTS(req);
    }

    /**
     * 语音转文本（STT），支持流式和非流式，统一返回STTResponse
     */
    @PostMapping(value = "/stt", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<STTResponse> createTranscription(
        @PathVariable("tenantId") String tenantId,
        @RequestBody STTRequest req,
        ServerHttpResponse response
    ) {
        if (req.isStream()) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        } else {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
        return audioCommandHandler.handleSTT(req);
    }

    /**
     * 语音转文本（STT），支持文件上传，multipart/form-data
     */
    @PostMapping(value = "/stt/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<STTResponse> createTranscriptionWithFile(
        @PathVariable("tenantId") String tenantId,
        @RequestPart("req") String reqJson,
        @RequestPart("file") FilePart filePart,
        ServerHttpResponse response
    ) throws Exception {
        STTRequest req = objectMapper.readValue(reqJson, STTRequest.class);
        File tempFile = File.createTempFile("audio", ".tmp");
        // 用响应式方式保存文件
        return filePart.transferTo(tempFile)
            .thenMany(Flux.defer(() -> {
                if (req.getInput() != null) {
                    req.getInput().setAudioFile(tempFile);
                }
                boolean stream = req.isStream();
                if (stream) {
                    response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
                } else {
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                }
                return audioCommandHandler.handleSTT(req)
                    .doFinally(signalType -> tempFile.delete());
            }));
    }
   
} 