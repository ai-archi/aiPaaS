package com.aixone.llm.application.command.quota;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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