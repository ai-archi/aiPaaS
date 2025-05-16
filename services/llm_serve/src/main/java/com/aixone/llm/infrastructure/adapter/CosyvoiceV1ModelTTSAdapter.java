package com.aixone.llm.infrastructure.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.models.audio.TTSRequest;
import com.aixone.llm.domain.models.audio.TTSResponse;
import com.aixone.llm.domain.models.model.UserModelKey;
import com.aixone.llm.domain.services.AudioModelTTSAdapter;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;

import reactor.core.publisher.Flux;
@Component
public class CosyvoiceV1ModelTTSAdapter implements AudioModelTTSAdapter, ModelAdapterFactoryImpl.ModelNamed {

    @Override
    public Flux<TTSResponse> recognizeTTS(TTSRequest ttsRequest, UserModelKey key) {
        boolean isStream = ttsRequest.getStream() != null && ttsRequest.getStream();
        // 统一校验text参数
        String text = ttsRequest.getInput() != null ? ttsRequest.getInput().getText() : null;
        if (text == null || text.trim().isEmpty() || text.length() > 500) {
            throw new IllegalArgumentException("TTS请求text不能为空或超过500字符！");
        }
        String model = ttsRequest.getModel();
        String voice = ttsRequest.getInput() != null ? ttsRequest.getInput().getVoice() : "loongstella";
        String apiKeyStr = key != null ? key.getApiKey() : null;

        if (!isStream) {
            // 打印主要参数，便于排查
            System.out.println("[TTS][非流式] text=" + text + ", model=" + model + ", voice=" + voice);
            SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .model(model)
                .voice(voice)
                .apiKey(apiKeyStr)
                .build();
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);
            ByteBuffer audio;
            try {
                audio = synthesizer.call(text);
            } catch (Exception e) {
                System.err.println("[TTS][异常] DashScope调用失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("TTS合成失败: " + e.getMessage(), e);
            }
            if (audio == null) {
                System.err.println("[TTS] 合成失败，DashScope返回null。text=" + text + ", model=" + model + ", voice=" + voice);
                throw new RuntimeException("TTS合成失败，DashScope返回null，请检查text内容是否合法（不能包含特殊字符、不能全为标点、不能为无意义内容）");
            }
            String base64Audio = java.util.Base64.getEncoder().encodeToString(audio.array());
            TTSResponse.Audio audioObj = new TTSResponse.Audio();
            audioObj.setData(base64Audio);
            TTSResponse.Output output = new TTSResponse.Output();
            output.setAudio(audioObj);
            TTSResponse resp = new TTSResponse();
            resp.setOutput(output);
            return Flux.just(resp);
        } else {
            // 打印主要参数，便于排查
            System.out.println("[TTS][流式] text=" + text + ", model=" + model + ", voice=" + voice);
            // 流式合成：每帧推送 TTSResponse（DashScope 2.x 官方写法）
            return Flux.create(sink -> {
                ResultCallback<SpeechSynthesisResult> callback = new ResultCallback<>() {
                    @Override
                    public void onEvent(SpeechSynthesisResult message) {
                        if (message.getAudioFrame() != null) {
                            byte[] audioData = message.getAudioFrame().array();
                            String base64Audio = java.util.Base64.getEncoder().encodeToString(audioData);
                            TTSResponse.Audio audioObj = new TTSResponse.Audio();
                            audioObj.setData(base64Audio);
                            TTSResponse.Output output = new TTSResponse.Output();
                            output.setAudio(audioObj);
                            TTSResponse resp = new TTSResponse();
                            resp.setOutput(output);
                            sink.next(resp);
                        }
                    }
                    @Override
                    public void onComplete() {
                        sink.complete();
                    }
                    @Override
                    public void onError(Exception e) {
                        sink.error(e);
                    }
                };
                SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                    .model(model)
                    .voice(voice)
                    .apiKey(apiKeyStr)
                    .build();
                SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, callback);
                synthesizer.call(text); // 使用校验后的text
            });
        }
    }

    @Override
    public java.util.List<String> getModelNames() {
        // 返回支持的模型名列表
        return java.util.Arrays.asList("cosyvoice-v1");
    }   
} 