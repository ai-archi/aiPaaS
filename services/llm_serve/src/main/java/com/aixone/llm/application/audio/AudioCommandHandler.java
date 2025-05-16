package com.aixone.llm.application.audio;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.aixone.llm.domain.models.AudioWebSocketMessage;
import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
import com.aixone.llm.domain.services.ModelInvokeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioCommandHandler {
    private final ModelInvokeService modelInvokeService;

    public Flux<STTResponse> handleSTT(STTRequest request) {
        return modelInvokeService.invokeSTT(request);
    }

    public Flux<TTSResponse> handleTTS(TTSRequest request) {
        return modelInvokeService.invokeTTS(request);
    }
    /* 实时文本转语音（WebSocket流式） */
    public Mono<Void> handleRealtimeTTS(AudioWebSocketMessage wsMessage) {
        try {
            AudioWebSocketMessage.Header header = wsMessage.getHeader();
            Map<String, Object> payload = wsMessage.getPayload();

            // 2. 解析TTS参数（如model、input、parameters、resources、meta等）
            if (payload == null) {
                return Mono.error(new IllegalArgumentException("payload不能为空"));
            }
            String model = (String) payload.get("model");
            Map<String, Object> input = (Map<String, Object>) payload.get("input");
            Map<String, Object> parameters = (Map<String, Object>) payload.get("parameters");
            List<Map<String, Object>> resources = (List<Map<String, Object>>) payload.get("resources");
            Map<String, Object> meta = (Map<String, Object>) payload.get("meta");
            if (model == null || input == null) {
                return Mono.error(new IllegalArgumentException("TTS参数不完整"));
            }
            // 组装TTSRequest对象
            TTSRequest.Input ttsInput = new TTSRequest.Input();
            ttsInput.setText((String) input.get("text"));
            ttsInput.setVoice((String) input.get("voice"));
            ttsInput.setLanguage((String) input.get("language"));
            TTSRequest ttsRequest = new TTSRequest();
            ttsRequest.setModel(model);
            ttsRequest.setInput(ttsInput);
            ttsRequest.setParameters(parameters);
            ttsRequest.setResources(resources);
            ttsRequest.setMeta(meta);
            // 这里只组装参数，不处理WebSocketSession，实际推送需在Handler层实现
            modelInvokeService.invokeRealtimeTTS(
                model,
                ttsRequest,
                result -> {},
                error -> {}
            );
            return Mono.empty();
        } catch (Exception e) {
            log.error("TTS处理异常", e);
            return Mono.error(e);
        }
    }

    public Mono<Void> handleRealtimeSTT(AudioWebSocketMessage wsMessage) {
        return Mono.error(new UnsupportedOperationException("请实现AudioWebSocketMessage结构的解析与处理"));
    }

    public Mono<Void> handleUnknown(AudioWebSocketMessage wsMessage) {
        return Mono.error(new UnsupportedOperationException("请实现AudioWebSocketMessage结构的解析与处理"));
    }

    /**
     * 新增：WebSocket流式TTS，直接接收领域对象和session，推送结果到前端
     */
    public Mono<Void> handleRealtimeTTS(TTSRequest ttsRequest, WebSocketSession session) {
        try {
            String model = ttsRequest.getModel();
            // 这里假设 modelInvokeService.invokeRealtimeTTS 支持回调
            return Mono.create(sink -> {
                modelInvokeService.invokeRealtimeTTS(
                    model,
                    ttsRequest,
                    result -> {
                        // 推送TTS音频流结果到前端
                        session.send(Mono.just(session.textMessage(result))).subscribe();
                    },
                    error -> {
                        // 推送错误信息到前端
                        session.send(Mono.just(session.textMessage("{\"error\":\"TTS处理异常\"}"))).subscribe();
                        sink.error(error);
                    }
                );
                sink.success();
            });
        } catch (Exception e) {
            log.error("TTS处理异常", e);
            return session.send(Mono.just(session.textMessage("{\"error\":\"TTS处理异常\"}"))).then();
        }
    }
} 