package com.aixone.llm.interfaces.ws;

import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.aixone.llm.application.audio.AudioCommandHandler;
import com.aixone.llm.domain.models.AudioWebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class LLMServeWebSocketHandler implements WebSocketHandler {
    private final AudioCommandHandler audioCommandHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public @NonNull Mono<Void> handle(@NonNull WebSocketSession session) {
        return session.receive()
            .flatMap(message -> {
                String task = null;
                if (message.getType() == WebSocketMessage.Type.TEXT) {
                    try {
                        String payload = message.getPayloadAsText();
                        JsonNode node = objectMapper.readTree(payload);
                        if (node.has("payload") && node.get("payload").has("task")) {
                            task = node.get("payload").get("task").asText();
                        } else {
                            task = "";
                        }
                        if ("stt".equalsIgnoreCase(task)) {
                            session.getAttributes().put("task", task);
                        }
                        if ("tts".equalsIgnoreCase(task)) {
                            session.getAttributes().put("task", task);
                        }
                    } catch (JsonProcessingException e) {
                        log.error("WebSocket 文本消息解析异常", e);
                        return session.send(Mono.just(session.textMessage("{\"error\":\"解析失败\"}"))).then();
                    }
                } else if (message.getType() == WebSocketMessage.Type.BINARY) {
                    // type 应从 session attribute 获取
                    task = (String) session.getAttributes().get("task");
                }
                if (task == null) {
                    return session.send(Mono.just(session.textMessage("{\"error\":\"type不能为空\"}"))).then();
                }
                AudioWebSocketMessage wsMessage = handleMessage(session, message, task);
                if (wsMessage == null) {
                    return session.send(Mono.just(session.textMessage("{\"error\":\"消息封装失败\"}"))).then();
                }
                return switch (task) {
                    case "tts" -> handleRealtimeTTSMessage(session, wsMessage);
                    case "stt" -> audioCommandHandler.handleRealtimeSTT(wsMessage);
                    default -> audioCommandHandler.handleUnknown(wsMessage);
                };
            })
            .then();
    }

    private AudioWebSocketMessage handleMessage(WebSocketSession session, WebSocketMessage message, String type) {
        AudioWebSocketMessage wsMessage;
        try {
            switch (message.getType()) {
                case TEXT -> {
                    String payload = message.getPayloadAsText();
                    wsMessage = objectMapper.readValue(payload, AudioWebSocketMessage.class);
                }
                case BINARY -> {
                    wsMessage = new AudioWebSocketMessage();
                    AudioWebSocketMessage.Header header = new AudioWebSocketMessage.Header();
                    Object taskId = session.getAttributes().get("task_id");
                    if (taskId instanceof String) {
                        header.setTask_id((String) taskId);
                    }
                    wsMessage.setHeader(header);
                    java.util.Map<String, Object> payload = new java.util.HashMap<>();
                    payload.put("audio", message.getPayload().toByteBuffer());
                    payload.put("sessionId", session.getId());
                    payload.put("task", type);
                    wsMessage.setPayload(payload);
                }
                default -> wsMessage = null;
            }
        } catch (Exception e) {
            log.error("WebSocket 消息封装异常", e);
            wsMessage = null;
        }
        return wsMessage;
    }

    /**
     * 处理 TTS 消息，将 AudioWebSocketMessage 转换为 TTSRequest 并调用业务逻辑
     */
    private Mono<Void> handleRealtimeTTSMessage(WebSocketSession session, AudioWebSocketMessage wsMessage) {
        try {
            // 解析 payload
            var payload = wsMessage.getPayload();
            if (payload == null) {
                return session.send(Mono.just(session.textMessage("{\"error\":\"payload不能为空\"}"))).then();
            }
            String model = (String) payload.get("model");
            Map<String, Object> input = (Map<String, Object>) payload.get("input");
            Map<String, Object> parameters = (Map<String, Object>) payload.get("parameters");
            List<Map<String, Object>> resources = (List<Map<String, Object>>) payload.get("resources");
            Map<String, Object> meta = (Map<String, Object>) payload.get("meta");
            if (model == null || input == null || parameters == null) {
                return session.send(Mono.just(session.textMessage("{\"error\":\"TTS参数不完整\"}"))).then();
            }
            // 组装 TTSRequest
            com.aixone.llm.domain.models.audio.TTSRequest.Input ttsInput = new com.aixone.llm.domain.models.audio.TTSRequest.Input();
            ttsInput.setText((String) input.get("text"));
            ttsInput.setVoice((String) input.get("voice"));
            ttsInput.setLanguage((String) input.get("language"));
            com.aixone.llm.domain.models.audio.TTSRequest ttsRequest = new com.aixone.llm.domain.models.audio.TTSRequest();
            ttsRequest.setModel(model);
            ttsRequest.setInput(ttsInput);
            ttsRequest.setParameters(parameters);
            ttsRequest.setResources(resources);
            ttsRequest.setMeta(meta);
            // 调用业务逻辑
            return audioCommandHandler.handleRealtimeTTS(ttsRequest, session);
        } catch (Exception e) {
            log.error("TTS消息处理异常", e);
            return session.send(Mono.just(session.textMessage("{\"error\":\"TTS消息处理异常\"}"))).then();
        }
    }
} 