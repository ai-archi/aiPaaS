package com.aixone.workbench.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.application.dto.UserMenuCustomDTO;
import com.aixone.workbench.menu.domain.service.MenuAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/workbench/menus")
@Slf4j
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuAggregationService menuAggregationService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getVisibleMenus(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) List<String> roles) {
        
        log.info("获取用户可见菜单: userId={}, tenantId={}, roles={}", userId, tenantId, roles);
        
        if (userId == null || tenantId == null) {
            log.warn("userId 或 tenantId 为空，返回空菜单列表");
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
        
        try {
            UUID userIdUuid = convertToUUID(userId);
            UUID tenantIdUuid = convertToUUID(tenantId);
            List<UUID> rolesUuid = roles != null ? roles.stream()
                    .map(this::convertToUUID)
                    .toList() : List.of();
            
            List<MenuDTO> menus = menuAggregationService.aggregateVisibleMenus(
                    userIdUuid, tenantIdUuid, rolesUuid);
            
            return ResponseEntity.ok(ApiResponse.success(menus));
        } catch (IllegalArgumentException e) {
            log.warn("无法转换参数为UUID: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
    }
    
    private UUID convertToUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            log.debug("无法将 '{}' 转换为UUID，使用默认值", value);
            
            // 特殊处理：'default' 映射到测试租户UUID
            if ("default".equals(value)) {
                return UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
            
            // 对于数字字符串（如 "0"），转换为固定UUID
            if (value.matches("\\d+")) {
                // 返回全零UUID作为默认值
                return UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
            
            // 其他情况使用 hashCode 生成UUID
            long hash = value.hashCode();
            return new UUID(hash, hash);
        }
    }
    
    /**
     * 获取用户菜单个性化配置
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 用户菜单个性化配置
     */
    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<Map<String, UserMenuCustomDTO>>> getUserMenuCustom(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String tenantId) {
        
        log.info("获取用户菜单个性化配置: userId={}, tenantId={}", userId, tenantId);
        
        if (userId == null || tenantId == null) {
            log.warn("userId 或 tenantId 为空，返回空配置");
            return ResponseEntity.ok(ApiResponse.success(Map.of()));
        }
        
        try {
            UUID userIdUuid = convertToUUID(userId);
            UUID tenantIdUuid = convertToUUID(tenantId);
            
            Map<String, UserMenuCustomDTO> configs = menuAggregationService.getUserMenuCustomConfig(userIdUuid, tenantIdUuid);
            
            return ResponseEntity.ok(ApiResponse.success(configs));
        } catch (IllegalArgumentException e) {
            log.warn("无法转换参数为UUID: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(Map.of()));
        }
    }
    
    /**
     * 保存用户菜单个性化配置
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param menuId 菜单ID
     * @param config 个性化配置JSON
     * @return 操作结果
     */
    @PutMapping("/custom")
    public ResponseEntity<ApiResponse<Void>> saveUserMenuCustom(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String menuId,
            @RequestBody String config) {
        
        log.info("保存用户菜单个性化配置: userId={}, menuId={}", userId, menuId);
        
        if (userId == null || tenantId == null || menuId == null) {
            log.warn("参数不完整，无法保存配置");
            return ResponseEntity.ok(ApiResponse.badRequest("参数不完整"));
        }
        
        try {
            UUID userIdUuid = convertToUUID(userId);
            UUID tenantIdUuid = convertToUUID(tenantId);
            UUID menuIdUuid = UUID.fromString(menuId); 
            
            menuAggregationService.saveUserMenuCustom(userIdUuid, tenantIdUuid, menuIdUuid, config);
            
            return ResponseEntity.ok(ApiResponse.success());
        } catch (IllegalArgumentException e) {
            log.warn("无法转换参数为UUID: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.badRequest("参数格式错误"));
        }
    }
}
