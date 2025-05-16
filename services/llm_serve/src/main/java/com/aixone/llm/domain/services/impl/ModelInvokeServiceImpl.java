package com.aixone.llm.domain.services.impl;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
import com.aixone.llm.domain.models.chat.ChatRequest;
import com.aixone.llm.domain.models.chat.ChatResponse;
import com.aixone.llm.domain.models.completion.CompletionRequest;
import com.aixone.llm.domain.models.completion.CompletionResponse;
import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;
import com.aixone.llm.domain.models.model.ModelConfig;
import com.aixone.llm.domain.models.model.UserModelKey;
import com.aixone.llm.domain.repositories.model.ModelInvokeRepository;
import com.aixone.llm.domain.repositories.model.UserModelKeyRepository;
import com.aixone.llm.domain.services.AudioModelSTTAdapter;
import com.aixone.llm.domain.services.AudioModelTTSAdapter;
import com.aixone.llm.domain.services.ModelAdapter;
import com.aixone.llm.domain.services.ModelAdapterFactory;
import com.aixone.llm.domain.services.ModelInvokeService;
import com.aixone.llm.domain.services.ModelService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModelInvokeServiceImpl implements ModelInvokeService {
    private final ModelInvokeRepository modelInvokeRepository;
    private final ModelService modelService;
    private final ModelAdapterFactory modelAdapterFactory;
    private final UserModelKeyRepository userModelKeyRepository;
    private static final int MONITORING_WINDOW_SECONDS = 60;

    @Override
    public Flux<ChatResponse> invokeChat(ChatRequest request) {
        String modelName = request.getModel();
        String keyId = request.getKeyId();
        Mono<UserModelKey> keyMono;
        if (keyId != null && !keyId.isEmpty()) {
            keyMono = userModelKeyRepository.findById(keyId)
                .filter(key -> key != null && key.getModelName().equals(modelName));
        } else {
            keyMono = userModelKeyRepository.findByModelName(modelName).next();
        }
        return modelService.getModelByName(modelName)
            .filter(model -> model.isActive())
            .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
            .flatMapMany(model -> keyMono.switchIfEmpty(Mono.error(new IllegalStateException("No available key for model: " + modelName)))
                .flatMapMany(key -> {
                    ModelAdapter adapter = modelAdapterFactory.getAdapter(modelName);
                    if (adapter == null) {
                        return Flux.error(new IllegalStateException("No adapter found for model: " + modelName));
                    }
                    if (request.isStream()) {
                        return adapter.invokeChatStream(model, request, key)
                            .doOnComplete(() -> {
                                // 可在此保存最终响应记录
                            });
                    } else {
                        return adapter.invokeChat(model, request, key)
                            .doOnSuccess(response -> {
                                // 可在此保存响应记录
                            })
                            .flux();
                    }
                })
            );
    }

    @Override
    public Flux<CompletionResponse> invokeCompletion(CompletionRequest request) {
        String modelName = request.getModel();
        String keyId = request.getKeyId();
        Mono<UserModelKey> keyMono;
        if (keyId != null && !keyId.isEmpty()) {
            keyMono = userModelKeyRepository.findById(keyId)
                .filter(key -> key != null && key.getModelName().equals(modelName));
        } else {
            keyMono = userModelKeyRepository.findByModelName(modelName).next();
        }
        return modelService.getModelByName(modelName)
            .filter(model -> model.isActive())
            .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
            .flatMapMany(model -> keyMono.switchIfEmpty(Mono.error(new IllegalStateException("No available key for model: " + modelName)))
                .flatMapMany(key -> {
                    ModelAdapter adapter = modelAdapterFactory.getAdapter(modelName);
                    if (adapter == null) {
                        return Flux.error(new IllegalStateException("No adapter found for model: " + modelName));
                    }
                    if (request.isStream()) {
                        return adapter.invokeCompletionStream(model, request, key)
                            .doOnComplete(() -> {
                                // 可在此保存最终响应记录
                            });
                    } else {
                        return adapter.invokeCompletion(model, request, key)
                            .doOnSuccess(response -> {
                                // 可在此保存响应记录
                            })
                            .flux();
                    }
                })
            );
    }

    @Override
    public Mono<Long> getUsedTokens(String userId, String modelId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(5); // 最近5分钟的使用量
        return modelInvokeRepository.getUserTokenUsage(userId, modelId, start, now);
    }

    @Override
    public Mono<Boolean> checkModelAvailability(String modelId) {
        return modelService.getModel(modelId)
            .map(model -> model.isActive())
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Long> getModelLatency(String modelId) {
        return modelInvokeRepository.getRecentLatency(modelId, MONITORING_WINDOW_SECONDS);
    }

    @Override
    public Mono<Double> getModelErrorRate(String modelId) {
        return modelInvokeRepository.getRecentErrorRate(modelId, MONITORING_WINDOW_SECONDS);
    }

    @Override
    public Mono<ImageResponse> invokeImage(ImageRequest request) {
        final String type = (request.getType() == null) ? "generation" : request.getType();
        String modelName = request.getModel(); // 或自定义字段
        String keyId = request.getKeyId();
        // 用户可选指定keyId，未指定则自动路由
        Mono<UserModelKey> keyMono;
        if (keyId != null && !keyId.isEmpty()) {
            keyMono = userModelKeyRepository.findById(keyId)
                .filter(key -> key != null && key.getModelName().equals(modelName));
        } else {
            // 简单自动路由策略：取第一个可用key，实际可按性能/价格/优先级扩展
            keyMono = userModelKeyRepository.findByModelName(modelName).next();
        }
        return modelService.getModelByName(modelName)
                .filter(ModelConfig::isActive)
                .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
                .flatMap(model -> keyMono.switchIfEmpty(Mono.error(new IllegalStateException("No available key for model: " + modelName)))
                    .flatMap(key -> {
                        ModelAdapter adapter = modelAdapterFactory.getAdapter(modelName);
                        if (adapter == null) {
                            return Mono.error(new IllegalStateException("No adapter found for model: " + modelName));
                        }
                        return switch (type) {
                            case "generation" -> adapter.generateImage(model, request, key);
                            case "edit" -> adapter.editImage(model, request, key);
                            case "variation" -> adapter.variationImage(model, request, key);
                            default -> Mono.error(new IllegalArgumentException("不支持的图片处理类型: " + type));
                        };
                    })
                );
    }

    @Override
    public Mono<ImageTaskResponse> getImageTaskResult(String taskId, String modelName) {
        // 这里假设协议层可传递keyId，实际可根据业务调整
        // 若需支持协议层传递keyId，可扩展参数
        // 这里只做自动路由
        Mono<UserModelKey> keyMono = userModelKeyRepository.findByModelName(modelName).next();
        return modelService.getModelByName(modelName)
                .filter(ModelConfig::isActive)
                .switchIfEmpty(Mono.error(new IllegalStateException("Model is not available")))
                .flatMap(model -> keyMono.switchIfEmpty(Mono.error(new IllegalStateException("No available key for model: " + modelName)))
                    .flatMap(key -> {
                        ModelAdapter adapter = modelAdapterFactory.getAdapter(modelName);
                        if (adapter == null) {
                            return Mono.error(new IllegalStateException("No adapter found for model: " + modelName));
                        }
                        return adapter.getImageTaskResult(model, taskId, key);
                    })
                );
    }

    @Override
    public Flux<STTResponse> invokeSTT(STTRequest request) {
        // String modelName = request.getModel();
        // // 假设 STTRequest 增加了 InputStream audioStream 字段
        // final InputStream audioStream;
        // if (request instanceof com.aixone.llm.domain.models.audio.STTRequest) {
        //     InputStream tmp = null;
        //     try {
        //         java.lang.reflect.Field f = request.getClass().getDeclaredField("audioStream");
        //         f.setAccessible(true);
        //         tmp = (InputStream) f.get(request);
        //     } catch (Exception ignore) {}
        //     audioStream = tmp;
        // } else {
        //     audioStream = null;
        // }
        // if (audioStream == null) {
        //     return Flux.error(new IllegalArgumentException("STTRequest 缺少音频流 audioStream 字段"));
        // }
        // return userModelKeyRepository.findByModelName(modelName).next()
        //     .flatMapMany(key -> {
        //         AudioModelSTTAdapter sttAdapter = modelAdapterFactory.getAudioModelSTTAdapter(modelName);
        //         if (sttAdapter == null) {
        //             return Flux.error(new IllegalStateException("No WebSocket STT adapter found for model: " + modelName));
        //         }
        //         return Flux.create(sink -> {
        //             sttAdapter.recognizeSTT(audioStream, key, result -> {
        //                 STTResponse.Output output = new STTResponse.Output();
        //                 output.setText(result);
        //                 STTResponse resp = new STTResponse();
        //                 resp.setOutput(output);
        //                 sink.next(resp);
        //                 sink.complete();
        //             }, error -> {
        //                 sink.error(error);
        //             });
        //         });
        //     });
        return null;
    }

    @Override
    public Flux<TTSResponse> invokeTTS(TTSRequest request) {
        String modelName = request.getModel();
        return userModelKeyRepository.findByModelName(modelName).next()
            .flatMapMany(key -> {
                AudioModelTTSAdapter ttsAdapter = modelAdapterFactory.getAudioModelTTSAdapter(modelName);
                if (ttsAdapter == null) {
                    return Flux.error(new IllegalStateException("No WebSocket TTS adapter found for model: " + modelName));
                }
                try {
                    return ttsAdapter.recognizeTTS(request, key);
                } catch (Exception e) {
                    return Flux.error(e);
                }
            });
    }

    @Override
    public void invokeRealtimeSTT(String modelName, java.io.InputStream audioStream, java.util.function.Consumer<String> onResult, java.util.function.Consumer<Throwable> onError) {
        // var wsAdapter = modelAdapterFactory.getSpeechRecognitionWebSocketAdapter(modelName);
        // if (wsAdapter == null) {
        //     onError.accept(new IllegalArgumentException("No WebSocket STT adapter found for model: " + modelName));
        //     return;
        // }
        // // 查找模型key
        // userModelKeyRepository.findByModelName(modelName).next()
        //     .doOnError(onError)
        //     .subscribe(key -> {
        //         if (key == null) {
        //             onError.accept(new IllegalStateException("No available key for model: " + modelName));
        //         } else {
        //             wsAdapter.recognizeSTT(audioStream, key, onResult, onError);
        //         }
        //     }, onError);
    }

    @Override
    public void invokeRealtimeTTS(String modelName, TTSRequest ttsRequest, java.util.function.Consumer<String> onResult, java.util.function.Consumer<Throwable> onError) {
        // AudioModelTTSAdapter ttsAdapter = modelAdapterFactory.getAudioModelTTSAdapter(modelName);
        // if (ttsAdapter == null) {
        //     onResult.accept("No WebSocket STT adapter found for model: " + modelName);
        //     return;
        // }
        // // 查找模型key
        // userModelKeyRepository.findByModelName(modelName).next()
        //     .doOnError(onError)
        //     .subscribe(key -> {
        //         if (key == null) {
        //             onError.accept(new IllegalStateException("No available key for model: " + modelName));
        //         } else {
        //             try {
        //                 ttsAdapter.recognizeTTS(ttsRequest, key)
        //                     .map(resp -> resp.getOutput() != null && resp.getOutput().getAudio() != null ? resp.getOutput().getAudio().getData() : null)
        //                     .doOnNext(onResult)
        //                     .doOnError(onError)
        //                     .subscribe();
        //             } catch (Exception e) {
        //                 onError.accept(e);
        //             }
        //         }
        //     }, onError);
    }
} 