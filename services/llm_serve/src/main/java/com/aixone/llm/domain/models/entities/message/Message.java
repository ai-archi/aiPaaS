package com.aixone.llm.domain.models.entities.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import java.util.List;
/**
 * 对应 DeepSeek API 消息结构
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 消息ID，必填
     */
    private String id;

    /**
     * 消息内容，必填
     */
    private Object content;

    /**
     * 角色，必填（如 assistant/user/system/tool）
     */
    private String role;

    /**
     * 参与者名称，可选
     */
    private String name;

    /**
     * tool call 的 ID，可选
     */
    private String toolCallId;

    /**
     * 租户ID，可选
     */
    private String tenantId;

    /**
     * 创建时间，秒级时间戳
     */
    private Long createdAt;
} 