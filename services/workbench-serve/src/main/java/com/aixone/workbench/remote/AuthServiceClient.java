package com.aixone.workbench.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

/**
 * 认证与权限服务远程接口。
 */
@FeignClient(name = "auth-serve", url = "http://localhost:8082")
public interface AuthServiceClient {
    /**
     * 校验用户是否有某菜单权限
     */
    @GetMapping("/api/v1/permissions/check")
    boolean checkMenuPermission(@RequestParam("userId") UUID userId, @RequestParam("menuId") UUID menuId);
} 