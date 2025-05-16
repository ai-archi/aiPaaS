package com.aixone.llm.infrastructure.adapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
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



}

