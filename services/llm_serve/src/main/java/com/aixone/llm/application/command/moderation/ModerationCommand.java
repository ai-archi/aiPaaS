package com.aixone.llm.application.command.moderation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内容审核请求命令对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationCommand {
    /**
     * 待审核内容
     */
    private String input;
    /**
     * 审核模型（可选）
     */
    private String model;
    /**
     * 额外参数（可选）
     */
    private java.util.Map<String, Object> extraParams;
} 