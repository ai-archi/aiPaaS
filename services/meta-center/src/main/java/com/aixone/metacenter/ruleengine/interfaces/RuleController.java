package com.aixone.metacenter.ruleengine.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.metacenter.ruleengine.application.RuleApplicationService;
import com.aixone.metacenter.ruleengine.domain.Rule;
import com.aixone.metacenter.ruleengine.domain.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 规则引擎REST控制器
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleApplicationService ruleApplicationService;
    private final RuleEngineService ruleEngineService;

    /**
     * 执行规则
     * 
     * @param ruleId 规则ID
     * @param context 执行上下文
     * @return 执行结果
     */
    @PostMapping("/{ruleId}/execute")
    public ResponseEntity<ApiResponse<Object>> executeRule(
            @PathVariable Long ruleId,
            @RequestBody Map<String, Object> context) {
        log.info("执行规则: ruleId={}", ruleId);
        Rule rule = ruleApplicationService.getRuleById(ruleId);
        Object result = ruleEngineService.executeRule(rule, context);
        return ResponseEntity.ok(ApiResponse.success(result, "规则执行成功"));
    }

    /**
     * 批量执行规则
     * 
     * @param ruleIds 规则ID列表
     * @param context 执行上下文
     * @return 执行结果
     */
    @PostMapping("/batch-execute")
    public ResponseEntity<ApiResponse<List<Object>>> batchExecuteRules(
            @RequestParam List<Long> ruleIds,
            @RequestBody Map<String, Object> context) {
        log.info("批量执行规则: ruleIds={}", ruleIds);
        List<Object> results = ruleApplicationService.executeRules(ruleIds, context);
        return ResponseEntity.ok(ApiResponse.success(results, "规则批量执行成功"));
    }



    /**
     * 验证规则表达式
     * 
     * @param expression 规则表达式
     * @return 验证结果
     */
    @PostMapping("/validate-expression")
    public ResponseEntity<ApiResponse<Boolean>> validateExpression(@RequestBody String expression) {
        log.info("验证规则表达式");
        boolean isValid = ruleApplicationService.validateRuleExpression(expression);
        return ResponseEntity.ok(ApiResponse.success(isValid, "表达式验证完成"));
    }





    /**
     * 启用规则
     * 
     * @param ruleId 规则ID
     * @return 启用结果
     */
    @PostMapping("/{ruleId}/enable")
    public ResponseEntity<ApiResponse<Void>> enableRule(@PathVariable Long ruleId) {
        log.info("启用规则: {}", ruleId);
        ruleApplicationService.enableRule(ruleId);
        return ResponseEntity.ok(ApiResponse.success(null, "规则启用成功"));
    }

    /**
     * 禁用规则
     * 
     * @param ruleId 规则ID
     * @return 禁用结果
     */
    @PostMapping("/{ruleId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableRule(@PathVariable Long ruleId) {
        log.info("禁用规则: {}", ruleId);
        ruleApplicationService.disableRule(ruleId);
        return ResponseEntity.ok(ApiResponse.success(null, "规则禁用成功"));
    }


} 