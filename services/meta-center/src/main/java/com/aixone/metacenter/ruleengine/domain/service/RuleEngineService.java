package com.aixone.metacenter.ruleengine.domain.service;

import com.aixone.metacenter.ruleengine.domain.Rule;
import com.aixone.metacenter.ruleengine.domain.RuleRepository;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 规则引擎领域服务
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final RuleRepository ruleRepository;

    /**
     * 验证规则表达式
     * 
     * @param expression 规则表达式
     */
    public void validateRuleExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new MetaValidationException("规则表达式不能为空");
        }
        
        // TODO: 使用Aviator表达式引擎验证表达式语法
        log.debug("验证规则表达式: {}", expression);
    }

    /**
     * 执行规则
     * 
     * @param rule 规则
     * @param context 执行上下文
     * @return 执行结果
     */
    public Object executeRule(Rule rule, Object context) {
        log.debug("执行规则: {}", rule.getName());
        
        if (!rule.isEnabled()) {
            throw new MetaValidationException("规则已禁用: " + rule.getName());
        }
        
        // TODO: 使用Aviator表达式引擎执行规则
        // 这里需要将context转换为Map<String, Object>格式
        Map<String, Object> contextMap = convertContextToMap(context);
        return executeRuleInternal(rule, contextMap);
    }

    /**
     * 执行规则
     * 
     * @param tenantId 租户ID
     * @param ruleType 规则类型
     * @param context 执行上下文
     * @return 执行结果
     */
    public RuleExecutionResult executeRules(String tenantId, String ruleType, Map<String, Object> context) {
        log.debug("执行规则，租户ID: {}, 规则类型: {}", tenantId, ruleType);
        
        // 获取启用的规则
        List<Rule> rules = ruleRepository.findByTenantIdAndEnabledTrue(tenantId);
        
        RuleExecutionResult result = new RuleExecutionResult();
        
        for (Rule rule : rules) {
            if (ruleType.equals(rule.getType()) && rule.isActive()) {
                try {
                    boolean executed = executeRuleInternal(rule, context);
                    if (executed) {
                        result.addExecutedRule(rule.getName());
                    }
                } catch (Exception e) {
                    log.error("规则执行失败，规则名称: {}, 错误: {}", rule.getName(), e.getMessage());
                    result.addFailedRule(rule.getName(), e.getMessage());
                }
            }
        }
        
        return result;
    }

    /**
     * 执行单个规则（内部方法）
     * 
     * @param rule 规则
     * @param context 执行上下文
     * @return 是否执行成功
     */
    private boolean executeRuleInternal(Rule rule, Map<String, Object> context) {
        // TODO: 实现规则表达式解析和执行
        // 这里可以使用Aviator表达式引擎或其他规则引擎
        log.debug("执行规则: {}", rule.getName());
        return true;
    }

    /**
     * 将上下文对象转换为Map
     * 
     * @param context 上下文对象
     * @return Map格式的上下文
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertContextToMap(Object context) {
        if (context instanceof Map) {
            return (Map<String, Object>) context;
        }
        
        // TODO: 使用反射或其他方式将对象转换为Map
        // 这里暂时返回空Map，实际实现时需要根据具体需求处理
        log.warn("上下文对象类型不支持，需要实现转换逻辑: {}", context.getClass().getName());
        return new java.util.HashMap<>();
    }

    /**
     * 规则执行结果
     */
    public static class RuleExecutionResult {
        private final List<String> executedRules = new java.util.ArrayList<>();
        private final Map<String, String> failedRules = new java.util.HashMap<>();

        public void addExecutedRule(String ruleName) {
            executedRules.add(ruleName);
        }

        public void addFailedRule(String ruleName, String error) {
            failedRules.put(ruleName, error);
        }

        public List<String> getExecutedRules() {
            return executedRules;
        }

        public Map<String, String> getFailedRules() {
            return failedRules;
        }

        public boolean hasFailures() {
            return !failedRules.isEmpty();
        }
    }
} 