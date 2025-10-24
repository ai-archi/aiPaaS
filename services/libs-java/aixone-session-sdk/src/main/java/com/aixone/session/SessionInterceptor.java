package com.aixone.session;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring拦截器，统一解析Token、租户、ABAC属性，注入SessionContext
 */
public class SessionInterceptor implements HandlerInterceptor {
    private final TokenParser tokenParser;
    public SessionInterceptor(TokenParser tokenParser) {
        this.tokenParser = tokenParser;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            SessionContext.SessionInfo info = tokenParser.parse(token);
            SessionContext.set(info);
        } else {
            // 也可支持无Token场景，或抛出异常
            SessionContext.clear();
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SessionContext.clear();
    }
} 