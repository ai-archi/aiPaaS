package com.aixone.permission.context;

import com.aixone.permission.model.User;

/**
 * 权限上下文
 * 用于存储当前请求的用户信息等上下文数据
 */
public class PermissionContext {
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }
    public static User getUser() {
        return USER_HOLDER.get();
    }
    public static void clear() {
        USER_HOLDER.remove();
    }
} 