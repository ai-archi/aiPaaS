package com.aixone.llm.application.quota;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocateQuotaCommand {
    private String userId;
    private String modelId;
    private Long tokenLimit;
    private Long requestLimit;
    private LocalDateTime expiresAt;
    private String quotaType;  // TRIAL, PAID, ENTERPRISE
} 