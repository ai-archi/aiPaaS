package com.aixone.session;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring拦截器，统一解析Token、租户、ABAC属性，注入SessionContext
 * 支持多租户、权限验证和ABAC属性管理
 */
public class SessionInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);
    
    private final TokenParser tokenParser;
    private final boolean requireAuth;
    private final String tenantHeader;
    
    public SessionInterceptor(TokenParser tokenParser) {
        this(tokenParser, true, "X-Tenant-ID");
    }
    
    public SessionInterceptor(TokenParser tokenParser, boolean requireAuth) {
        this(tokenParser, requireAuth, "X-Tenant-ID");
    }
    
    public SessionInterceptor(TokenParser tokenParser, boolean requireAuth, String tenantHeader) {
        this.tokenParser = tokenParser;
        this.requireAuth = requireAuth;
        this.tenantHeader = tenantHeader;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 清理之前的上下文
            SessionContext.clear();
            
            // 获取Token
            String token = extractToken(request);
            
            if (token != null) {
                // 验证Token有效性
                if (!tokenParser.isValid(token)) {
                    logger.warn("Invalid token provided for request: {}", request.getRequestURI());
                    if (requireAuth) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return false;
                    }
                } else {
                    // 解析Token并设置上下文
                    SessionContext.SessionInfo sessionInfo = tokenParser.parse(token);
                    
                    // 验证租户ID（如果提供了租户头）
                    String headerTenantId = request.getHeader(tenantHeader);
                    if (headerTenantId != null && !headerTenantId.equals(sessionInfo.getTenantId())) {
                        logger.warn("Tenant ID mismatch: token tenant={}, header tenant={}", 
                                sessionInfo.getTenantId(), headerTenantId);
                        if (requireAuth) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return false;
                        }
                    }
                    
                    // 检查会话是否过期
                    if (sessionInfo.isExpired()) {
                        logger.warn("Session expired for user: {}", sessionInfo.getUserId());
                        if (requireAuth) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return false;
                        }
                    }
                    
                    // 设置会话上下文
                    SessionContext.set(sessionInfo);
                    logger.debug("Session context set for user: {}, tenant: {}", 
                            sessionInfo.getUserId(), sessionInfo.getTenantId());
                }
            } else if (requireAuth) {
                logger.warn("No token provided for protected resource: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing session context for request: {}", request.getRequestURI(), e);
            if (requireAuth) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
            return true;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理会话上下文
        SessionContext.clear();
    }
    
    /**
     * 从请求中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    /**
     * 创建不需要认证的拦截器实例
     */
    public static SessionInterceptor createOptional(TokenParser tokenParser) {
        return new SessionInterceptor(tokenParser, false);
    }
    
    /**
     * 创建需要认证的拦截器实例
     */
    public static SessionInterceptor createRequired(TokenParser tokenParser) {
        return new SessionInterceptor(tokenParser, true);
    }
} 