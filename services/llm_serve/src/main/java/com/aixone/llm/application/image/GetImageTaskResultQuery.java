package com.aixone.llm.application.image;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 获取图片处理任务结果的查询对象。
 * 封装任务ID。
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
public class GetImageTaskResultQuery {
    private String taskId;
} 