package com.aixone.workbench.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 仪表盘DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private String name;
    private String layout;
    private List<ComponentDTO> components;
    private String config;
    
    // 仪表盘欢迎语
    private String remark;
    
    // 统计数据
    private StatisticsDTO statistics;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ComponentDTO implements Serializable {
    private String id;
    private String type;
    private String title;
    private Object dataSource;
    private Object config;
}

