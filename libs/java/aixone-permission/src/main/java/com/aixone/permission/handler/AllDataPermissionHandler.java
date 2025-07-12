package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 全量数据权限处理器
 * 返回1=1，表示不过滤任何数据
 */
public class AllDataPermissionHandler implements DataPermissionHandler {
    @Override
    public String buildCondition(User user, Resource resource) {
        return "1=1";
    }
} 