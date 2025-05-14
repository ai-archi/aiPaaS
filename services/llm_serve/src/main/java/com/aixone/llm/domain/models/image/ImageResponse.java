package com.aixone.llm.domain.models.image;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OpenAI 图片接口兼容响应模型。
 * created: 时间戳
 * data: 图片结果列表（url或b64_json）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    /**
     * 创建时间戳
     */
    private long created;
    /**
     * 图片结果数据
     */
    private List<ImageData> data;
    /**
     * 任务ID（阿里云等异步任务场景）
     */
    private String taskId;
    /**
     * 请求ID（阿里云等异步任务场景）
     */
    private String requestId;
    /**
     * 任务状态（如PENDING、SUCCEEDED等，阿里云等异步任务场景）
     */
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        /**
         * 图片URL（response_format为url时）
         */
        private String url;
        /**
         * 图片base64内容（response_format为b64_json时）
         */
        private String b64_json;
    }
} 