package com.aixone.permission.sql;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * SQL条件生成器扩展点
 * 负责根据用户、资源、数据权限类型生成SQL条件
 */
public interface SqlConditionBuilder {
    /**
     * 生成SQL条件
     * @param user 用户对象
     * @param resource 资源对象
     * @param dataType 数据权限类型
     * @return SQL条件片段
     */
    String build(User user, Resource resource, String dataType);
} 