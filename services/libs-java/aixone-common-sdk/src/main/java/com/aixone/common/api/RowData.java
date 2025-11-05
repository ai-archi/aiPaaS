package com.aixone.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行数据包装类
 * 用于baTable等组件期望的 {row: {...}} 格式
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowData<T> {
    
    /**
     * 行数据
     */
    private T row;
}

