package com.aixone.llm.domain.models.completion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionRequest {

    /**
     * possible values: [deepseek-chat, deepseek-reasoner]
     */
    
    private String model;

    /**
     * 用于生成完成内容的提示，必填
     */
    private String prompt;

        /**
     * 可选，如果设置为 true，则在输出中，把 prompt 的内容也输出出来。
     */
    private boolean echo;

    /**
     * 可选，介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其在已有文本中的出现频率受到相应的惩罚，降低模型重复相同内容的可能性。
     */
    private Double frequencyPenalty;
    /**
     * 可选，指定输出 token 的对数概率，最大 20。
     */
    private Integer logprobs;
    /**
     * 可选，介于 1 到 8192 间的整数，限制一次请求中模型生成 completion 的最大 token 数。
     * 输入 token 和输出 token 的总长度受模型的上下文长度的限制。如未指定 max_tokens参数，默认使用 4096。
     */
    private Integer maxTokens;

    /**
     * 可选，介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其是否已在已有文本中出现受到相应的惩罚，从而增加模型谈论新主题的可能性。
     */
    private Double presencePenalty;

    /**
     * 停止词，可选
     */
    private List<String> stop;

    /**
     * 是否流式返回，可选,如果设置为 True，将会以 SSE（server-sent events）的形式以流式发送消息增量。
     * 消息流以 data: [DONE] 结尾。
     */
    private boolean stream;

    /**
     * 如果设置为 true，在流式消息最后的 data: [DONE] 之前将会传输一个额外的块。
     * 此块上的 usage 字段显示整个请求的 token 使用统计信息，而 choices 字段将始终是一个空数组。
     * 所有其他块也将包含一个 usage 字段，但其值为 null。
     */
    private Map<String, Object> streamOptions;



    private String suffix;

    /**
     * 采样温度，可选采样温度，介于 0 和 2 之间。更高的值，如 0.8，会使输出更随机，而更低的值，如 0.2，会使其更加集中和确定。 我们通常建议可以更改这个值或者更改 top_p，但不建议同时对两者进行修改。
     */
    private Double temperature;

    /**
     * top_p采样，可选，作为调节采样温度的替代方案，模型会考虑前 top_p 概率的 token 的结果。
     * 所以 0.1 就意味着只有包括在最高 10% 概率中的 token 会被考虑。
     * 我们通常建议修改这个值或者更改 temperature，但不建议同时对两者进行修改。
     */
    private Double topP;






    /**
     * 租户ID，可选
     */
    @JsonIgnore
    private String tenantId;
    
    
} 