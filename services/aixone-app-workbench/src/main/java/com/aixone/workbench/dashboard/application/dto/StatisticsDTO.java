package com.aixone.workbench.dashboard.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 仪表盘统计数据DTO
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 会员注册数
    private Long userRegNumber;
    
    // 附件上传数
    private Long fileNumber;
    
    // 总会员数
    private Long usersNumber;
    
    // 已安装插件数
    private Long addonsNumber;
}

