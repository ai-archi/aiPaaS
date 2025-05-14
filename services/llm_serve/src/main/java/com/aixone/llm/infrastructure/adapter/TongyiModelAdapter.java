package com.aixone.llm.infrastructure.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.aixone.llm.domain.models.audio.ASRRequest;
import com.aixone.llm.domain.models.audio.AudioMessage;
import com.aixone.llm.domain.models.audio.AudioResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;
import com.aixone.llm.domain.models.model.ModelConfig;
import com.aixone.llm.domain.models.model.UserModelKey;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TongyiModelAdapter extends OpenAIModelAdapter {
    private static final Logger log = LoggerFactory.getLogger(TongyiModelAdapter.class);


    public TongyiModelAdapter(WebClient.Builder webClientBuilder) {
        super(webClientBuilder,"https://dashscope.aliyuncs.com/api/v1");
        

    }

    @Override
    public List<String> getModelNames() {
        return Arrays.asList("wanx2.1-imageedit","wanx2.1-imageedit-v2",
        "wanx2.1-t2i-turbo","wanx2.1-t2i-plus","wanx2.0-t2i-turbo","wanx-style-repaint-v1",
        "qwen-tts","paraformer-v2",
        "stable-diffusion-3.5-large","stable-diffusion-3.5-large-turbo");
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TongyiImageTaskResponse {
        private String request_id;
        private Output output;
    
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Output {
            private String task_id;
            private String task_status;
        }
    } 
    @Override
    public Mono<ImageResponse> generateImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        if (request.getPrompt() != null) input.put("prompt", request.getPrompt());
        if (request.getNegative_prompt() != null) input.put("negative_prompt", request.getNegative_prompt());
        body.put("input", input);
        Map<String, Object> parameters = new HashMap<>();
        if (request.getN() != null) parameters.put("n", request.getN());
        if (request.getParameters() != null) parameters.putAll(request.getParameters());
        body.put("parameters", parameters);
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis";
        String authHeader = "Bearer " + key.getApiKey();
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.debug("[TongyiModelAdapter] [generateImage] 请求URL: {}", url);
            log.debug("[TongyiModelAdapter] Header: X-DashScope-Async: enable");
            log.debug("[TongyiModelAdapter] Header: Authorization: {}", authHeader);
            log.debug("[TongyiModelAdapter] Header: Content-Type: application/json");
            log.debug("[TongyiModelAdapter] 请求体: {}", jsonBody);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.debug("[TongyiModelAdapter] 请求体序列化失败: {}", e.getMessage());
        }
        return webClient.post()
                .uri("/services/aigc/text2image/image-synthesis")
                .header("X-DashScope-Async", "enable")
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TongyiImageTaskResponse.class)
                .map(tongyiResp -> {
                    try {
                        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(tongyiResp);
                        log.debug("[TongyiModelAdapter] [generateImage] 响应内容: {}", json);
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        log.debug("[TongyiModelAdapter] [generateImage] 响应内容序列化失败: {}", e.getMessage());
                    }
                    ImageResponse resp = new ImageResponse();
                    resp.setCreated(0L);
                    resp.setRequestId(tongyiResp.getRequest_id());
                    resp.setTaskId(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_id() : null);
                    resp.setData(null);
                    resp.setStatus(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_status() : null);
                    return resp;
                });
    }

    @Override
    public Mono<ImageResponse> editImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        if (request.getFunction() != null) input.put("function", request.getFunction());
        if (request.getPrompt() != null) input.put("prompt", request.getPrompt());
        if (request.getImage() != null) input.put("base_image_url", request.getImage());
        if (request.getMask() != null) input.put("mask_image_url", request.getMask());
        body.put("input", input);
        Map<String, Object> parameters = new HashMap<>();
        if (request.getN() != null) parameters.put("n", request.getN());
        if (request.getParameters() != null) parameters.putAll(request.getParameters());
        body.put("parameters", parameters);
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/image-synthesis";
        String authHeader = "Bearer " + key.getApiKey();
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.debug("[TongyiModelAdapter] [editImage] 请求URL: {}", url);
            log.debug("[TongyiModelAdapter] Header: X-DashScope-Async: enable");
            log.debug("[TongyiModelAdapter] Header: Authorization: {}", authHeader);
            log.debug("[TongyiModelAdapter] Header: Content-Type: application/json");
            log.debug("[TongyiModelAdapter] 请求体: {}", jsonBody);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.debug("[TongyiModelAdapter] 请求体序列化失败: {}", e.getMessage());
        }
        return webClient.post()
                .uri("/services/aigc/image2image/image-synthesis")
                .header("X-DashScope-Async", "enable")
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TongyiImageTaskResponse.class)
                .map(tongyiResp -> {
                    try {
                        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(tongyiResp);
                        log.debug("[TongyiModelAdapter] [editImage] 响应内容: {}", json);
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        log.debug("[TongyiModelAdapter] [editImage] 响应内容序列化失败: {}", e.getMessage());
                    }
                    ImageResponse resp = new ImageResponse();
                    resp.setCreated(0L);
                    resp.setRequestId(tongyiResp.getRequest_id());
                    resp.setTaskId(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_id() : null);
                    resp.setData(null);
                    resp.setStatus(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_status() : null);
                    return resp;
                });
    }

    @Override
    public Mono<ImageResponse> variationImage(ModelConfig model, ImageRequest request, UserModelKey key) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        if (request.getPrompt() != null) input.put("prompt", request.getPrompt());
        if (request.getImage() != null) input.put("base_image_url", request.getImage());
        body.put("input", input);
        Map<String, Object> parameters = new HashMap<>();
        if (request.getN() != null) parameters.put("n", request.getN());
        if (request.getParameters() != null) parameters.putAll(request.getParameters());
        body.put("parameters", parameters);
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/image-variation";
        String authHeader = "Bearer " + key.getApiKey();
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.debug("[TongyiModelAdapter] [variationImage] 请求URL: {}", url);
            log.debug("[TongyiModelAdapter] Header: X-DashScope-Async: enable");
            log.debug("[TongyiModelAdapter] Header: Authorization: {}", authHeader);
            log.debug("[TongyiModelAdapter] Header: Content-Type: application/json");
            log.debug("[TongyiModelAdapter] 请求体: {}", jsonBody);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.debug("[TongyiModelAdapter] 请求体序列化失败: {}", e.getMessage());
        }
        return webClient.post()
                .uri("/services/aigc/image2image/image-variation")
                .header("X-DashScope-Async", "enable")
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TongyiImageTaskResponse.class)
                .map(tongyiResp -> {
                    try {
                        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(tongyiResp);
                        log.debug("[TongyiModelAdapter] [variationImage] 响应内容: {}", json);
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        log.debug("[TongyiModelAdapter] [variationImage] 响应内容序列化失败: {}", e.getMessage());
                    }
                    ImageResponse resp = new ImageResponse();
                    resp.setCreated(0L);
                    resp.setRequestId(tongyiResp.getRequest_id());
                    resp.setTaskId(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_id() : null);
                    resp.setData(null);
                    resp.setStatus(tongyiResp.getOutput() != null ? tongyiResp.getOutput().getTask_status() : null);
                    return resp;
                });
    }

    @Override
    public Mono<ImageTaskResponse> getImageTaskResult(ModelConfig model, String taskId, UserModelKey key) {
        String url = "https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId;
        String authHeader = "Bearer " + key.getApiKey();

        log.debug("[TongyiModelAdapter] 查询任务URL: {}", url);
        log.debug("[TongyiModelAdapter] Header: Authorization: {}", authHeader);
        log.debug("[TongyiModelAdapter] Header: Content-Type: application/json");
        log.debug("[TongyiModelAdapter] GET请求无body");

        return webClient.get()
                .uri("/tasks/" + taskId)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(ImageTaskResponse.class)
                .doOnNext(resp -> {
                    try {
                        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(resp);
                        log.debug("[TongyiModelAdapter] 查询任务响应内容: {}", json);
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        log.debug("[TongyiModelAdapter] 查询任务响应内容序列化失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 调用阿里云Paraformer录音文件识别API，file_urls为公网可访问音频URL数组，详见官方文档：
     * https://help.aliyun.com/zh/model-studio/paraformer-recorded-speech-recognition-restful-api
     */
    @Override
    public Mono<AudioResponse> invokeASR(ModelConfig model, ASRRequest request, UserModelKey key) {
        String url = "/services/audio/asr/transcription";
        String authHeader = "Bearer " + key.getApiKey();
        List<String> fileUrls = request.getFileUrls();
        if (fileUrls == null || fileUrls.isEmpty()) {
            log.error("[TongyiModelAdapter][invokeASR] fileUrls不能为空，且必须为公网可访问的音频URL");
            return Mono.error(new IllegalArgumentException("fileUrls不能为空，且必须为公网可访问的音频URL"));
        }
        if (model.getName() == null || model.getName().isEmpty()) {
            log.error("[TongyiModelAdapter][invokeASR] model不能为空");
            return Mono.error(new IllegalArgumentException("model不能为空"));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        input.put("file_urls", fileUrls);
        if (request.getAudioFormat() != null) input.put("audio_format", request.getAudioFormat());
        if (request.getLanguage() != null) input.put("language", request.getLanguage());
        body.put("input", input);
        log.info("[TongyiModelAdapter][invokeASR] Header: Authorization: {}", authHeader);
        log.info("[TongyiModelAdapter][invokeASR] Header: Content-Type: application/json");
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.info("[TongyiModelAdapter][invokeASR] RequestBody: {}", jsonBody);
        } catch (JsonProcessingException e) {
            log.warn("[TongyiModelAdapter][invokeASR] RequestBody序列化失败: {}", e.getMessage());
        }
        return webClient.post()
                .uri(url)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("X-DashScope-Async", "enable")
                .bodyValue(body)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                        log.error("[TongyiModelAdapter][invokeASR] 阿里云ASR接口失败响应: {}", errorBody);
                        return Mono.error(new RuntimeException("Aliyun ASR error: " + errorBody));
                    })
                )
                .bodyToMono(Map.class)
                .doOnNext(resp -> {
                    try {
                        String json = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(resp);
                        log.info("[TongyiModelAdapter][invokeASR] 响应内容: {}", json);
                    } catch (JsonProcessingException e) {
                        log.warn("[TongyiModelAdapter][invokeASR] 响应内容序列化失败: {}", e.getMessage());
                    }
                })
                .map(resp -> {
                    AudioResponse response = new AudioResponse();
                    response.setType("asr");
                    Map<String, Object> output = (Map<String, Object>) resp.get("output");
                    String text = output != null ? (String) output.get("text") : null;
                    AudioMessage msg = new AudioMessage();
                    msg.setRole("assistant");
                    msg.setContent(text);
                    response.setMessages(Collections.singletonList(msg));
                    response.setOutput(null);
                    response.setRequestId((String) resp.get("request_id"));
                    return response;
                });
    }

    @Override
    public Mono<AudioResponse> invokeTTS(ModelConfig model, TTSRequest request, UserModelKey key) {
        String url = "/services/aigc/multimodal-generation/generation";
        String authHeader = "Bearer " + key.getApiKey();
        String text = request.getText();
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        input.put("text", text);
        input.put("voice", request.getVoice());
        if (request.getLanguage() != null) input.put("language", request.getLanguage());
        body.put("input", input);
        // 打印请求header和body
        log.info("[TongyiModelAdapter][invokeTTS] Header: Authorization: {}", authHeader);
        log.info("[TongyiModelAdapter][invokeTTS] Header: Content-Type: application/json");
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.info("[TongyiModelAdapter][invokeTTS] RequestBody: {}", jsonBody);
        } catch (JsonProcessingException e) {
            log.warn("[TongyiModelAdapter][invokeTTS] RequestBody序列化失败: {}", e.getMessage());
        }
        return webClient.post()
            .uri(url)
            .header("Authorization", authHeader)
            .header("Content-Type", "application/json")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .map(resp -> mapTTSResponse(resp));
    }

    @Override
    public Flux<AudioResponse> invokeTTSStream(ModelConfig model, TTSRequest request, UserModelKey key) {
        String url = "/services/aigc/multimodal-generation/generation";
        String authHeader = "Bearer " + key.getApiKey();
        String text = request.getText();
        Map<String, Object> body = new HashMap<>();
        body.put("model", model.getName());
        Map<String, Object> input = new HashMap<>();
        input.put("text", text);
        input.put("voice", request.getVoice());
        if (request.getLanguage() != null) input.put("language", request.getLanguage());
        body.put("input", input);
        log.info("[TongyiModelAdapter][invokeTTSStream] Header: Authorization: {}", authHeader);
        log.info("[TongyiModelAdapter][invokeTTSStream] Header: Content-Type: application/json");
        log.info("[TongyiModelAdapter][invokeTTSStream] Header: X-DashScope-SSE: enable");
        try {
            String jsonBody = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(body);
            log.info("[TongyiModelAdapter][invokeTTSStream] RequestBody: {}", jsonBody);
        } catch (JsonProcessingException e) {
            log.warn("[TongyiModelAdapter][invokeTTSStream] RequestBody序列化失败: {}", e.getMessage());
        }
        return webClient.post()
            .uri(url)
            .header("Authorization", authHeader)
            .header("Content-Type", "application/json")
            .header("X-DashScope-SSE", "enable")
            .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
            .bodyValue(body)
            .retrieve()
            .bodyToFlux(Map.class)
            .map(resp -> mapTTSResponse(resp));
    }

    private AudioResponse mapTTSResponse(Map resp) {
        AudioResponse response = new AudioResponse();
        response.setType("tts");
        Map<String, Object> output = (Map<String, Object>) resp.get("output");
        String audioBase64 = null;
        if (output != null && output.get("audio") instanceof Map) {
            Map<String, Object> audioObj = (Map<String, Object>) output.get("audio");
            audioBase64 = (String) audioObj.get("data");
        }
        AudioMessage msg = new AudioMessage();
        msg.setRole("assistant");
        msg.setContent(audioBase64);
        response.setMessages(Collections.singletonList(msg));
        response.setOutput(output != null ? parseOutput(output) : null);
        response.setRequestId((String) resp.get("request_id"));
        return response;
    }

    private AudioResponse.Output parseOutput(Map<String, Object> output) {
        String finishReason = (String) output.get("finish_reason");
        Map<String, Object> audioObj = (Map<String, Object>) output.get("audio");
        AudioResponse.Audio audio = null;
        if (audioObj != null) {
            audio = new AudioResponse.Audio(
                audioObj.get("expires_at") != null ? Long.valueOf(audioObj.get("expires_at").toString()) : null,
                (String) audioObj.get("data"),
                (String) audioObj.get("id"),
                (String) audioObj.get("url")
            );
        }
        return new AudioResponse.Output(finishReason, audio);
    }

    @Override
    public Flux<AudioResponse> invokeASRStream(ModelConfig model, ASRRequest request, UserModelKey key) {
        // 目前API不支持流式，直接用单次Mono包装为Flux
        return invokeASR(model, request, key).flux();
    }

}

