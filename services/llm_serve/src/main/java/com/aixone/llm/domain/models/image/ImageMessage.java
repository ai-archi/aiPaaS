package com.aixone.llm.domain.models.image;

/**
 * 图片任务消息/步骤实体。
 * 可用于描述图片编辑/生成过程中的每一步或日志。
 */
public class ImageMessage {
    /**
     * 消息类型（如 info、warning、error、step 等）。
     */
    private String type;

    /**
     * 消息内容。
     */
    private String content;

    /**
     * 时间戳（可选）。
     */
    private long timestamp;

    public ImageMessage() {}

    public ImageMessage(String type, String content, long timestamp) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 