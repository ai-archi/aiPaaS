package com.aixone.permission.sql;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 部门数据权限SQL生成器
 * 生成按部门过滤的SQL条件
 */
public class DeptSqlConditionBuilder implements SqlConditionBuilder {
    @Override
    public String build(User user, Resource resource, String dataType) {
        Object deptId = user.getAttributes() != null ? user.getAttributes().get("deptId") : null;
        if (deptId == null) return "1=0";
        return "dept_id = '" + deptId + "'";
    }
} 