package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 仅本人数据权限处理器
 * 只返回与用户ID匹配的数据
 */
public class SelfDataPermissionHandler implements DataPermissionHandler {
    @Override
    public String buildCondition(User user, Resource resource) {
        if (user.getUserId() == null) return "1=0";
        return "user_id = '" + user.getUserId() + "'";
    }
} 