package com.aixone.llm.application.command.chat;

import com.aixone.llm.domain.models.values.config.ModelRequest;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
/**
 * 对应 DeepSeek /chat/completions API 的请求参数
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class ChatCompletionCommand extends ModelRequest {


    public ModelRequest toModelRequest() {
        return  (ModelRequest)this;
    }
} 