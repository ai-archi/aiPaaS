package com.aixone.metacenter.processengine.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.metacenter.processengine.application.ProcessApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 流程引擎REST控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/processes")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessApplicationService processApplicationService;

    /**
     * 启动流程
     * 
     * @param processId 流程ID
     * @param variables 流程变量
     * @return 启动结果
     */
    @PostMapping("/{processId}/start")
    public ResponseEntity<ApiResponse<Object>> startProcess(
            @PathVariable Long processId,
            @RequestBody Map<String, Object> variables) {
        log.info("启动流程实例: processId={}", processId);
        Object result = processApplicationService.startProcess(processId, variables);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "流程实例启动成功"));
    }
} 