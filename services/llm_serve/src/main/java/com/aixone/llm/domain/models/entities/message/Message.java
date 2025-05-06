package com.aixone.llm.domain.models.entities.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String threadId;
    private String role;
    private String content;
    private LocalDateTime timestamp;
} 