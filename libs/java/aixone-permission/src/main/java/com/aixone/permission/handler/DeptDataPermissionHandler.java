package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 部门数据权限处理器
 * 只返回与用户部门匹配的数据
 */
public class DeptDataPermissionHandler implements DataPermissionHandler {
    @Override
    public String buildCondition(User user, Resource resource) {
        Object deptId = user.getAttributes() != null ? user.getAttributes().get("deptId") : null;
        if (deptId == null) return "1=0";
        return "dept_id = '" + deptId + "'";
    }
} 