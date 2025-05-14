package com.aixone.llm.domain.services.impl;

import org.springframework.stereotype.Service;

import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;
import com.aixone.llm.domain.services.ImageService;
import com.aixone.llm.domain.services.ModelInvokeService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * OpenAI风格图片处理服务实现。
 * 实际业务中可对接第三方API或自研模型。
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ModelInvokeService modelInvokeService;

    @Override
    public Mono<ImageResponse> submitImageTask(ImageRequest request) {
        return modelInvokeService.invokeImage(request);
    }

    @Override
    public Mono<ImageTaskResponse> getImageTaskResult(String taskId, String modelName) {
        return modelInvokeService.getImageTaskResult(taskId, modelName);
    }
}
