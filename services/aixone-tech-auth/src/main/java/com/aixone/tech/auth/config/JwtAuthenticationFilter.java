package com.aixone.tech.auth.config;

import com.aixone.common.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 从请求头中提取Token，解析权限信息并设置到Spring Security上下文
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // 跳过公开接口，不需要JWT认证
        String requestPath = request.getRequestURI();
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 从请求头中提取Token
        String token = extractTokenFromRequest(request);
        
        if (token != null && jwtUtils.validateToken(token)) {
            // 提取权限信息
            Set<String> permissions = jwtUtils.getPermissionsFromToken(token);
            if (permissions == null) {
                permissions = Collections.emptySet();
            }
            
            // 转换为Spring Security的GrantedAuthority
            Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
            
            // 创建Authentication对象
            String userId = jwtUtils.getUserIdFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId, 
                null, 
                authorities
            );
            
            // 设置到Spring Security上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 判断是否为公开路径（不需要JWT认证）
     */
    private boolean isPublicPath(String path) {
        // request.getRequestURI() 返回完整路径（包含 context-path /api/v1）
        // 所以需要检查完整路径
        return path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/refresh") ||
               path.equals("/api/v1/auth/logout") || path.equals("/api/v1/auth/validate") ||
               path.startsWith("/api/v1/auth/sms/") || path.startsWith("/api/v1/auth/email/") ||
               path.startsWith("/api/v1/verification-codes/");
    }
    
    /**
     * 从请求头中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

