package com.aixone.workbench.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.UUID;

/**
 * 目录服务远程接口，获取菜单主数据。
 */
@FeignClient(name = "directory-serve", url = "http://localhost:8081")
public interface DirectoryServiceClient {
    /**
     * 获取指定租户的菜单主数据
     */
    @GetMapping("/api/v1/menus/main")
    List<Object> getMainMenus(@RequestParam("tenantId") UUID tenantId);
} 