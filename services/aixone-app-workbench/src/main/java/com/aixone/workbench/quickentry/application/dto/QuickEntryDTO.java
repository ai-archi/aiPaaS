package com.aixone.workbench.quickentry.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 快捷入口DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickEntryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private UUID entryId;
    private UUID menuId;
    private String name;
    private String icon;
    private Integer displayOrder;
    private String config;
}

