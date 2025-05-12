package com.aixone.llm.domain.models.image;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图片生成/编辑请求领域模型。
 * 支持图片生成与编辑的通用参数，参考阿里云通用图像编辑API。
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    /**
     * 操作类型：如 generation（生成）、edit（编辑）等。
     */
    private String type;

    /**
     * 编辑功能，如 stylization_all、doodle、control_cartoon_feature 等。
     */
    private String function;

    /**
     * 文本提示词。
     */
    private String prompt;

    /**
     * 原始图片URL（编辑时必填，生成时可为空）。
     */
    private String baseImageUrl;

    /**
     * 额外参数（如 is_sketch、strength、n 等）。
     */
    private Map<String, Object> parameters;
} 