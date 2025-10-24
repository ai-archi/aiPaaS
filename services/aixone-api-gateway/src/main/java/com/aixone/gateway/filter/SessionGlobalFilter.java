package com.aixone.gateway.filter;

import com.aixone.session.SessionContext;
import com.aixone.session.TokenParser;
import com.aixone.session.SessionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Spring Cloud Gateway 全局过滤器，统一解析 Token/租户/权限上下文，注入 SessionContext
 */
@Component
public class SessionGlobalFilter implements GlobalFilter, Ordered {
    @Value("${security.jwt.secret:secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                TokenParser parser = new TokenParser(jwtSecret);
                SessionContext.SessionInfo info = parser.parse(token);
                SessionContext.set(info);
            } catch (Exception e) {
                throw new SessionException("Token解析失败", e);
            }
        } else {
            SessionContext.clear();
        }
        // 透传租户ID等上下文到下游服务（如加到请求头）
        String tenantId = SessionContext.getTenantId();
        if (tenantId != null) {
            exchange = exchange.mutate().request(
                exchange.getRequest().mutate().header("X-Tenant-Id", tenantId).build()
            ).build();
        }
        return chain.filter(exchange).doFinally(signal -> SessionContext.clear());
    }

    @Override
    public int getOrder() {
        return -100; // 优先级高于大多数内置过滤器
    }
} 