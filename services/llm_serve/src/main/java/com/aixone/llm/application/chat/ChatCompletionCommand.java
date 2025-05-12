package com.aixone.llm.application.chat;

import com.aixone.llm.domain.models.chat.ChatRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
/**
 * 对应 DeepSeek /chat/completions API 的请求参数
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatCompletionCommand extends ChatRequest {


    public ChatRequest toChatRequest() {
        return  (ChatRequest)this;
    }
} 