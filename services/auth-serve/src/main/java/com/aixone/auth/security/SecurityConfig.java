package com.aixone.auth.security;

import com.aixone.auth.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import org.springframework.http.HttpHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/api/v1/tenants/*/register-enabled").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().disable()
            .httpBasic().disable()
            // 添加Token黑名单校验过滤器
            .addFilterBefore(new TokenBlacklistFilter(tokenBlacklistService), AbstractPreAuthenticatedProcessingFilter.class);
        return http.build();
    }

    /**
     * Token黑名单校验过滤器
     */
    public static class TokenBlacklistFilter extends AbstractPreAuthenticatedProcessingFilter {
        private final TokenBlacklistService tokenBlacklistService;
        public TokenBlacklistFilter(TokenBlacklistService tokenBlacklistService) {
            this.tokenBlacklistService = tokenBlacklistService;
        }
        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            return null;
        }
        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            return null;
        }
        protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (tokenBlacklistService.isBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":40101,\"message\":\"Token已失效\"}");
                    return;
                }
            }
            chain.doFilter(request, response);
        }
    }
} 