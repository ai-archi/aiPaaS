package com.aixone.llm.domain.event.assistant;

/**
 * 消息处理完成事件
 */
public class MessageProcessedEvent extends AssistantEvent {
    private final String threadId;
    private final String messageId;
    private final MessageProcessResult result;

    public MessageProcessedEvent(String assistantId, int version,
                               String threadId, String messageId,
                               MessageProcessResult result) {
        super("MESSAGE_PROCESSED", assistantId, version);
        this.threadId = threadId;
        this.messageId = messageId;
        this.result = result;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getMessageId() {
        return messageId;
    }

    public MessageProcessResult getResult() {
        return result;
    }
}

/**
 * 消息处理结果
 */
record MessageProcessResult(
    ProcessStatus status,
    String response,
    long tokensUsed,
    long processingTime
) {}

/**
 * 处理状态枚举
 */
enum ProcessStatus {
    SUCCESS,
    FAILED,
    TIMEOUT,
    QUOTA_EXCEEDED
} 