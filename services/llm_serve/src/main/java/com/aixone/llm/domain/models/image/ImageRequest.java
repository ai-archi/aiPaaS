package com.aixone.llm.domain.models.image;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 兼容OpenAI /images/create（generations/edits/variations）API的图片请求模型，并支持厂商扩展。
 * 参考：https://platform.openai.com/docs/api-reference/images/create
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    /**
     * 请求类型，可选值：generations、edits、variations。
     */
    private String type;
    /**
     * 原始图片（base64、url或文件ID）。
     * OpenAI: edits/variations必填，generations无需填写。
     * 阿里云：必填。
     * 百度：必填。
     * 商汤：必填。
     */
    private String image;

    /**
     * 文本提示词。
     * OpenAI: 必填（generations/edits），variations无需填写。
     */
    private String prompt;

    /**
     * 负面提示词。
     * OpenAI: 可选。
     */
    private String negative_prompt;
    /**
     * 背景设置（如纯色、透明、图片URL等，具体取决于厂商能力）。
     * 可选。
     */
    private String background;
    /**
     * 蒙版图片（base64、url或文件ID）。
     * OpenAI: 仅edits可选。
     */
    private String mask;
    /**
     * 模型名称（如需多模型路由时使用）。
     * 可选。
     */
    private String model;
    /**
     * 图片内容审核选项（如是否启用审核、审核策略、审核标签等，具体取决于厂商能力）。
     * 可选。
     */
    private String moderation;

    /**
     * 生成图片数量。
     * OpenAI: 可选。
     */
    private Integer n;

    /**
     * 图片压缩级别（0-100%）。
     * 仅支持gpt-image-1的webp或jpeg输出格式，默认100%。
     * 可选。
     */
    private String output_compression;


    /**
     * 图片输出格式（如"webp"、"jpeg"）。
     * 仅支持gpt-image-1的webp或jpeg输出格式，默认"webp"。
     * 可选。
     */
    private String output_format;



    /**
     * 图片质量（0-100%）。
     * 仅支持gpt-image-1的webp或jpeg输出格式，默认100%。
     * 可选。
     */
    private String quality;



    /**
     * 图片输出格式（如"url"、"b64_json"）。
     * 仅支持gpt-image-1的webp或jpeg输出格式，默认"webp"。
     * 可选。
     */
    private String response_format;

    /**
     * 图片尺寸（如"256x256"、"512x512"、"1024x1024"）。
     * OpenAI: 可选。
     */
    private String size;

    /**
     * 图片质量（0-100%）。
     * 仅支持gpt-image-1的webp或jpeg输出格式，默认100%。
     * 可选。
     */
    private String style;
    /**
     * 用户标识。
     * OpenAI: 可选。
     */
    private String user;

    /**
     * 用户可选指定的KeyId（如未指定则自动路由最优Key）。
     */
    private String keyId;

    





    // ----------- 扩展字段（兼容阿里云/百度/商汤等） -----------
    /**
     * 编辑功能（如 doodle、stylization_all、control_cartoon_feature 等）。
     * 阿里云API function参数，部分场景必填。OpenAI无此参数。
     */
    private String function;

    /**
     * 额外参数（如 is_sketch、strength、厂商自定义参数）。
     * 推荐所有厂商扩展参数均放入此Map，避免主结构膨胀。
     */
    private Map<String, Object> parameters;





} 