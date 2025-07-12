package com.aixone.permission.filter;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * 权限上下文过滤器
 * 用于在请求入口初始化和清理权限上下文
 */
public class PermissionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO: 解析用户信息并写入PermissionContext
        try {
            chain.doFilter(request, response);
        } finally {
            // 清理上下文
            // PermissionContext.clear();
        }
    }

    @Override
    public void destroy() {}
} 