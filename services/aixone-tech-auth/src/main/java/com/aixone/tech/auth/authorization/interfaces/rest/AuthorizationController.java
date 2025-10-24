package com.aixone.tech.auth.authorization.interfaces.rest;

import com.aixone.tech.auth.authorization.application.service.AuthorizationApplicationService;
import com.aixone.tech.auth.authorization.application.dto.CheckPermissionRequest;
import com.aixone.tech.auth.authorization.application.dto.CheckPermissionResponse;
import com.aixone.audit.application.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 授权控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthorizationApplicationService authorizationApplicationService;
    private final AuditService auditService;

    public AuthorizationController(AuthorizationApplicationService authorizationApplicationService,
                                   AuditService auditService) {
        this.authorizationApplicationService = authorizationApplicationService;
        this.auditService = auditService;
    }

    /**
     * 权限校验接口
     */
    @PostMapping("/check-permission")
    public ResponseEntity<CheckPermissionResponse> checkPermission(@RequestBody CheckPermissionRequest request) {
        CheckPermissionResponse response = authorizationApplicationService.checkPermission(request);
        
        // 记录权限校验审计日志
        auditService.logPermissionCheck(
            request.getUserId(),
            request.getResource(),
            request.getAction(),
            response.isAllowed() ? "SUCCESS" : "FAILURE",
            response.isAllowed() ? null : "权限不足"
        );
        
        return ResponseEntity.ok(response);
    }
}
