package com.aixone.llm.domain.services;

import com.aixone.llm.domain.models.image.ImageRequest;
import com.aixone.llm.domain.models.image.ImageResponse;
import com.aixone.llm.domain.models.image.ImageTaskResponse;

import reactor.core.publisher.Mono;

/**
 * 图片处理领域服务接口。
 * 提供图片生成与编辑的统一入口，领域层只关注业务规则，不涉及具体实现。
 */
public interface ImageService {
    /**
     * 发起图片生成/编辑异步请求，返回任务ID等初步信息。
     */
    Mono<ImageResponse> submitImageTask(ImageRequest request);

    /**
     * 根据任务ID获取图片处理结果。
     * @param taskId 任务ID
     * @return 图片处理结果
     */
    Mono<ImageTaskResponse> getImageTaskResult(String taskId, String model);
} 