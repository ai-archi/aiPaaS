package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;

/**
 * 数据权限处理器扩展点
 * 负责根据用户和资源生成数据权限条件
 */
public interface DataPermissionHandler {
    /**
     * 生成数据权限SQL片段
     * @param user 用户对象
     * @param resource 资源对象
     * @return SQL条件片段，如"dept_id = 1"
     */
    String buildCondition(User user, Resource resource);
} 