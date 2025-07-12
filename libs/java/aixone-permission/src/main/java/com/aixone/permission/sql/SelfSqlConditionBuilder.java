package com.aixone.permission.sql;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 本人数据权限SQL生成器
 * 生成仅本人可见的SQL条件
 */
public class SelfSqlConditionBuilder implements SqlConditionBuilder {
    @Override
    public String build(User user, Resource resource, String dataType) {
        if (user.getUserId() == null) return "1=0";
        return "user_id = '" + user.getUserId() + "'";
    }
} 