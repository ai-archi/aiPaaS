package com.aixone.llm.domain.services.impl;

import com.aixone.llm.domain.services.AudioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
public class AudioServiceImpl implements AudioService {
    @Override
    public Mono<String> transcribe(MultipartFile file) {
        // 返回模拟音频转写结果
        return Mono.just("{\"text\":\"音频转写成功\"}");
    }

    @Override
    public Mono<String> translate(MultipartFile file) {
        // 返回模拟音频翻译结果
        return Mono.just("{\"translation\":\"音频翻译成功\"}");
    }
} 