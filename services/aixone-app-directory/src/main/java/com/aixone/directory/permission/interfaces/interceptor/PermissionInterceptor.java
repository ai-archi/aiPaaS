package com.aixone.directory.permission.interfaces.interceptor;

import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionRuleApplicationService;
import com.aixone.directory.permission.application.PermissionRuleDto;
import com.aixone.directory.permission.domain.service.PermissionDecisionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 权限拦截器
 * 用于拦截管理接口（/api/v1/admin/**），根据权限规则进行权限验证
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionRuleApplicationService permissionRuleApplicationService;
    private final PermissionDecisionService permissionDecisionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // 只拦截管理接口
        if (!path.startsWith("/api/v1/admin/")) {
            return true;
        }
        
        // 从SessionContext获取租户ID和用户ID
        String tenantId = SessionContext.getTenantId();
        String userId = SessionContext.getUserId();
        
        if (!StringUtils.hasText(tenantId)) {
            log.warn("权限验证失败：未提供租户信息，path={}, method={}", path, method);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        if (!StringUtils.hasText(userId)) {
            log.warn("权限验证失败：未提供用户信息，path={}, method={}", path, method);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        // 查找匹配的权限规则
        List<PermissionRuleDto.PermissionRuleView> rules = permissionRuleApplicationService
                .findPermissionRulesByPathAndMethod(tenantId, path, method);
        
        if (rules.isEmpty()) {
            // 如果没有匹配的权限规则，默认允许访问（待权限决策引擎实现后，需要调用权限决策）
            log.debug("未找到匹配的权限规则，允许访问: path={}, method={}", path, method);
            return true;
        }
        
        // 获取优先级最高的权限规则（已按优先级排序）
        PermissionRuleDto.PermissionRuleView rule = rules.get(0);
        String requiredPermission = rule.getPermission();
        
        log.debug("找到匹配的权限规则: path={}, method={}, permission={}", path, method, requiredPermission);
        
        // 调用权限决策引擎进行权限验证
        // 解析权限标识（格式：{resource}:{action} 或 admin:{resource}:{action}）
        String permissionIdentifier = requiredPermission;
        if (requiredPermission.startsWith("admin:")) {
            // 如果是admin:前缀，去掉前缀
            permissionIdentifier = requiredPermission.substring(6);
        }
        
        boolean hasPermission = permissionDecisionService.checkPermissionByIdentifier(
                userId, tenantId, permissionIdentifier, null);
        
        if (!hasPermission) {
            log.warn("权限验证失败: userId={}, permission={}, path={}, method={}", userId, requiredPermission, path, method);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        
        log.debug("权限验证通过: userId={}, permission={}, path={}, method={}", userId, requiredPermission, path, method);
        return true;
    }
}

