package com.aixone.workbench.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作台索引控制器
 * 提供健康检查和基本信息接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/")
public class IndexController {
    
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "AixOne Workbench Service");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("description", "AixOne应用平台工作台服务");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "aixone-app-workbench");
        response.put("description", "AixOne应用平台工作台服务");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

