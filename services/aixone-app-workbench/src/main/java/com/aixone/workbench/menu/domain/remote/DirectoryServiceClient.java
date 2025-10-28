package com.aixone.workbench.menu.domain.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 目录服务客户端接口
 * 用于从directory-serve拉取菜单主数据
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@FeignClient(name = "directory-serve", path = "/api/v1/directory", fallback = DirectoryServiceClient.DirectoryServiceClientFallback.class)
public interface DirectoryServiceClient {
    
    /**
     * 获取菜单主数据
     * 
     * @param tenantId 租户ID
     * @return 菜单列表
     */
    @GetMapping("/menus")
    List<Map<String, Object>> getMenus(@RequestParam("tenantId") UUID tenantId);
    
    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色ID列表
     */
    @GetMapping("/users/{userId}/roles")
    List<UUID> getUserRoles(@PathVariable("userId") UUID userId, @RequestParam("tenantId") UUID tenantId);
    
    /**
     * 目录服务降级处理
     */
    class DirectoryServiceClientFallback implements DirectoryServiceClient {
        @Override
        public List<Map<String, Object>> getMenus(UUID tenantId) {
            return List.of();
        }
        
        @Override
        public List<UUID> getUserRoles(UUID userId, UUID tenantId) {
            return List.of();
        }
    }
}
