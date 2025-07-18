package com.aixone.metacenter.ruleengine.interfaces;

import com.aixone.metacenter.common.response.ApiResponse;
import com.aixone.metacenter.ruleengine.application.RuleApplicationService;
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
        Object result = ruleEngineService.executeRule(ruleId, context);
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
        List<Object> results = ruleEngineService.batchExecuteRules(ruleIds, context);
        return ResponseEntity.ok(ApiResponse.success(results, "规则批量执行成功"));
    }

    /**
     * 根据元数据对象ID执行规则
     * 
     * @param metaObjectId 元数据对象ID
     * @param context 执行上下文
     * @return 执行结果
     */
    @PostMapping("/meta-object/{metaObjectId}/execute")
    public ResponseEntity<ApiResponse<List<Object>>> executeRulesByMetaObject(
            @PathVariable Long metaObjectId,
            @RequestBody Map<String, Object> context) {
        log.info("根据元数据对象执行规则: metaObjectId={}", metaObjectId);
        List<Object> results = ruleEngineService.executeRulesByMetaObject(metaObjectId, context);
        return ResponseEntity.ok(ApiResponse.success(results, "规则执行成功"));
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
        boolean isValid = ruleEngineService.validateExpression(expression);
        return ResponseEntity.ok(ApiResponse.success(isValid, "表达式验证完成"));
    }

    /**
     * 测试规则
     * 
     * @param ruleId 规则ID
     * @param testData 测试数据
     * @return 测试结果
     */
    @PostMapping("/{ruleId}/test")
    public ResponseEntity<ApiResponse<Object>> testRule(
            @PathVariable Long ruleId,
            @RequestBody Map<String, Object> testData) {
        log.info("测试规则: ruleId={}", ruleId);
        Object result = ruleEngineService.testRule(ruleId, testData);
        return ResponseEntity.ok(ApiResponse.success(result, "规则测试完成"));
    }

    /**
     * 获取规则执行日志
     * 
     * @param ruleId 规则ID
     * @param limit 限制条数
     * @return 执行日志
     */
    @GetMapping("/{ruleId}/logs")
    public ResponseEntity<ApiResponse<List<Object>>> getRuleExecutionLogs(
            @PathVariable Long ruleId,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("获取规则执行日志: ruleId={}, limit={}", ruleId, limit);
        List<Object> logs = ruleEngineService.getRuleExecutionLogs(ruleId, limit);
        return ResponseEntity.ok(ApiResponse.success(logs, "获取日志成功"));
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

    /**
     * 获取规则统计信息
     * 
     * @param ruleId 规则ID
     * @return 统计信息
     */
    @GetMapping("/{ruleId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRuleStats(@PathVariable Long ruleId) {
        log.info("获取规则统计信息: {}", ruleId);
        Map<String, Object> stats = ruleEngineService.getRuleStats(ruleId);
        return ResponseEntity.ok(ApiResponse.success(stats, "获取统计信息成功"));
    }

    /**
     * 清理规则缓存
     * 
     * @param ruleId 规则ID
     * @return 清理结果
     */
    @PostMapping("/{ruleId}/clear-cache")
    public ResponseEntity<ApiResponse<Void>> clearRuleCache(@PathVariable Long ruleId) {
        log.info("清理规则缓存: {}", ruleId);
        ruleEngineService.clearRuleCache(ruleId);
        return ResponseEntity.ok(ApiResponse.success(null, "缓存清理成功"));
    }

    /**
     * 获取规则引擎状态
     * 
     * @return 引擎状态
     */
    @GetMapping("/engine/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEngineStatus() {
        log.info("获取规则引擎状态");
        Map<String, Object> status = ruleEngineService.getEngineStatus();
        return ResponseEntity.ok(ApiResponse.success(status, "获取状态成功"));
    }

    /**
     * 重新加载规则引擎
     * 
     * @return 重载结果
     */
    @PostMapping("/engine/reload")
    public ResponseEntity<ApiResponse<Void>> reloadEngine() {
        log.info("重新加载规则引擎");
        ruleEngineService.reloadEngine();
        return ResponseEntity.ok(ApiResponse.success(null, "引擎重载成功"));
    }
} 