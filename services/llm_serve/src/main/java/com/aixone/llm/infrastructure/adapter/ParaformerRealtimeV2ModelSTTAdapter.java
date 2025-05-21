package com.aixone.llm.infrastructure.adapter;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.audio.STTRequest;
import com.aixone.llm.domain.models.audio.STTResponse;
import com.aixone.llm.domain.models.model.UserModelKey;
import com.aixone.llm.domain.services.AudioModelSTTAdapter;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import reactor.core.publisher.Flux;

@Component
public class ParaformerRealtimeV2ModelSTTAdapter implements AudioModelSTTAdapter, ModelAdapterFactoryImpl.ModelNamed {
    private static final Logger logger = LoggerFactory.getLogger(ParaformerRealtimeV2ModelSTTAdapter.class);

    @Override
    public Flux<STTResponse> recognizeSTT(STTRequest request, UserModelKey key) {
        STTRequest.Input input = request.getInput();
        if (input == null) {
            throw new IllegalArgumentException("input不能为空");
        }
        String model = request.getModel();
        String apiKey = key != null ? key.getApiKey() : null;
        String format = input.getFormat();
        Integer sampleRate = input.getSampleRate();
        File audioFile = input.getAudioFile();
        boolean isStream = request.isStream();
        if (model == null || audioFile == null) {
            throw new IllegalArgumentException("model和audioFile不能为空");
        }
        RecognitionParam param = RecognitionParam.builder()
                .apiKey(apiKey)
                .model(model)
                .format(format)
                .sampleRate(sampleRate)
                .parameter("language_hints", new String[]{"zh", "en"})
                .build();
        Recognition recognizer = new Recognition();
        if (!isStream) {
            // 非流式：一次性返回全部结果
            try {
                String result = recognizer.call(param, audioFile);
                STTResponse.Output output = new STTResponse.Output();
                output.setText(result);
                STTResponse resp = new STTResponse();
                resp.setOutput(output);
                return Flux.just(resp);
            } catch (Exception e) {
                logger.error("非流式识别异常", e);
                return Flux.error(e);
            }
        } else {
            // 流式：将文件转为Flowable<ByteBuffer>，仅推送最终句子结果
            io.reactivex.Flowable<java.nio.ByteBuffer> audioSource = io.reactivex.Flowable.create(emitter -> {
                try (java.io.InputStream in = new java.io.FileInputStream(audioFile)) {
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        emitter.onNext(java.nio.ByteBuffer.wrap(buffer, 0, read));
                        Thread.sleep(20); // 控制推送速率
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }, io.reactivex.BackpressureStrategy.BUFFER);
            return Flux.create(sink -> {
                try {
                    recognizer.streamCall(param, audioSource)
                            .blockingForEach(result -> {
                                if (result.isSentenceEnd() && result.getSentence() != null) {
                                    STTResponse.Output output = new STTResponse.Output();
                                    output.setText(result.getSentence().getText());
                                    STTResponse resp = new STTResponse();
                                    resp.setOutput(output);
                                    sink.next(resp);
                                }
                            });
                } catch (ApiException | NoApiKeyException e) {
                    logger.error("流式识别异常", e);
                    sink.error(e);
                } finally {
                    sink.complete();
                }
            });
        }
    }

    @Override
    public java.util.List<String> getModelNames() {
        // 如需扩展模型名，建议配置化
        return java.util.Arrays.asList("paraformer-realtime-v2");
    }
} 