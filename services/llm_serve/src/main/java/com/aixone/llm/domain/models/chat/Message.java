package com.aixone.llm.domain.models.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
     * 消息内容，支持 String、List、Map 等多种类型
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

    /**
     * 获取 content 的字符串表示
     */
    public String getContentAsString() {
        if (content == null) return null;
        if (content instanceof String string) {
            return string;
        }
        try {
            // 其它类型转为 JSON 字符串
            return new ObjectMapper().writeValueAsString(content);
        } catch (JsonProcessingException e) {
            return content.toString();
        }
    }
} 