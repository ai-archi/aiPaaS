package com.aixone.metacenter.ruleengine.application;

import com.aixone.metacenter.ruleengine.domain.Rule;
import com.aixone.metacenter.ruleengine.domain.RuleRepository;
import com.aixone.metacenter.ruleengine.domain.service.RuleEngineService;
import com.aixone.metacenter.common.exception.MetaNotFoundException;
import com.aixone.metacenter.common.exception.MetaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 规则应用服务
 * 负责规则管理的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RuleApplicationService {

    private final RuleRepository ruleRepository;
    private final RuleEngineService ruleEngineService;

    /**
     * 创建规则
     *
     * @param rule 规则实体
     * @return 创建的规则
     */
    public Rule createRule(Rule rule) {
        log.info("创建规则: {}", rule.getName());
        
        // 验证规则名称唯一性
        if (ruleRepository.existsByName(rule.getName())) {
            throw new MetaValidationException("规则名称已存在: " + rule.getName());
        }
        
        // 验证规则表达式
        ruleEngineService.validateRuleExpression(rule.getExpression());
        
        // 设置创建时间
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        
        // 保存规则
        Rule savedRule = ruleRepository.save(rule);
        
        log.info("规则创建成功: {}", savedRule.getId());
        return savedRule;
    }

    /**
     * 更新规则
     *
     * @param id 规则ID
     * @param rule 规则实体
     * @return 更新后的规则
     */
    public Rule updateRule(Long id, Rule rule) {
        log.info("更新规则: {}", id);
        
        Rule existingRule = ruleRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("规则不存在: " + id));
        
        // 验证规则名称唯一性（排除自身）
        if (!existingRule.getName().equals(rule.getName()) &&
            ruleRepository.existsByName(rule.getName())) {
            throw new MetaValidationException("规则名称已存在: " + rule.getName());
        }
        
        // 验证规则表达式
        ruleEngineService.validateRuleExpression(rule.getExpression());
        
        // 更新规则属性
        existingRule.setName(rule.getName());
        existingRule.setDescription(rule.getDescription());
        existingRule.setExpression(rule.getExpression());
        existingRule.setPriority(rule.getPriority());
        existingRule.setEnabled(rule.getEnabled());
        existingRule.setUpdatedAt(LocalDateTime.now());
        
        // 保存规则
        Rule savedRule = ruleRepository.save(existingRule);
        
        log.info("规则更新成功: {}", id);
        return savedRule;
    }

    /**
     * 删除规则
     *
     * @param id 规则ID
     */
    public void deleteRule(Long id) {
        log.info("删除规则: {}", id);
        
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("规则不存在: " + id));
        
        ruleRepository.delete(rule);
        log.info("规则删除成功: {}", id);
    }

    /**
     * 根据ID查询规则
     *
     * @param id 规则ID
     * @return 规则
     */
    @Transactional(readOnly = true)
    public Rule getRuleById(Long id) {
        log.debug("查询规则: {}", id);
        
        return ruleRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("规则不存在: " + id));
    }

    /**
     * 根据名称查询规则
     *
     * @param name 规则名称
     * @return 规则
     */
    @Transactional(readOnly = true)
    public Optional<Rule> getRuleByName(String name) {
        log.debug("根据名称查询规则: {}", name);
        
        return ruleRepository.findByName(name);
    }

    /**
     * 分页查询规则
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<Rule> getRules(Pageable pageable) {
        log.debug("分页查询规则");
        
        return ruleRepository.findAll(pageable);
    }

    /**
     * 查询所有启用的规则
     *
     * @return 启用的规则列表
     */
    @Transactional(readOnly = true)
    public List<Rule> getEnabledRules() {
        log.debug("查询启用的规则");
        
        return ruleRepository.findByEnabledTrue();
    }

    /**
     * 根据优先级查询规则
     *
     * @param priority 优先级
     * @return 规则列表
     */
    @Transactional(readOnly = true)
    public List<Rule> getRulesByPriority(Integer priority) {
        log.debug("根据优先级查询规则: {}", priority);
        
        return ruleRepository.findByPriority(priority);
    }

    /**
     * 启用规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    public Rule enableRule(Long id) {
        log.info("启用规则: {}", id);
        
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("规则不存在: " + id));
        
        rule.setEnabled(true);
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepository.save(rule);
    }

    /**
     * 禁用规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    public Rule disableRule(Long id) {
        log.info("禁用规则: {}", id);
        
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new MetaNotFoundException("规则不存在: " + id));
        
        rule.setEnabled(false);
        rule.setUpdatedAt(LocalDateTime.now());
        
        return ruleRepository.save(rule);
    }

    /**
     * 执行规则
     *
     * @param ruleId 规则ID
     * @param context 执行上下文
     * @return 执行结果
     */
    public Object executeRule(Long ruleId, Object context) {
        log.debug("执行规则: {}", ruleId);
        
        Rule rule = getRuleById(ruleId);
        
        if (!rule.getEnabled()) {
            throw new MetaValidationException("规则已禁用: " + ruleId);
        }
        
        return ruleEngineService.executeRule(rule, context);
    }

    /**
     * 批量执行规则
     *
     * @param ruleIds 规则ID列表
     * @param context 执行上下文
     * @return 执行结果列表
     */
    public List<Object> executeRules(List<Long> ruleIds, Object context) {
        log.debug("批量执行规则: {}", ruleIds);
        
        return ruleIds.stream()
                .map(ruleId -> executeRule(ruleId, context))
                .toList();
    }

    /**
     * 检查规则名称是否存在
     *
     * @param name 规则名称
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return ruleRepository.existsByName(name);
    }

    /**
     * 验证规则表达式
     *
     * @param expression 规则表达式
     * @return 是否有效
     */
    public boolean validateRuleExpression(String expression) {
        log.debug("验证规则表达式: {}", expression);
        
        try {
            ruleEngineService.validateRuleExpression(expression);
            return true;
        } catch (Exception e) {
            log.warn("规则表达式验证失败: {}", e.getMessage());
            return false;
        }
    }



    /**
     * 根据租户ID查询规则列表
     *
     * @param tenantId 租户ID
     * @return 规则列表
     */
    @Transactional(readOnly = true)
    public List<Rule> getRulesByTenantId(String tenantId) {
        log.debug("根据租户ID查询规则列表: {}", tenantId);
        
        return ruleRepository.findByTenantId(tenantId);
    }

    /**
     * 根据租户ID和状态查询规则列表
     *
     * @param tenantId 租户ID
     * @param status 状态
     * @return 规则列表
     */
    @Transactional(readOnly = true)
    public List<Rule> getRulesByTenantIdAndStatus(String tenantId, String status) {
        log.debug("根据租户ID和状态查询规则列表: {}, {}", tenantId, status);
        
        return ruleRepository.findByTenantIdAndStatus(tenantId, status);
    }

    /**
     * 根据租户ID查询启用的规则列表
     *
     * @param tenantId 租户ID
     * @return 启用的规则列表
     */
    @Transactional(readOnly = true)
    public List<Rule> getEnabledRulesByTenantId(String tenantId) {
        log.debug("根据租户ID查询启用的规则列表: {}", tenantId);
        
        return ruleRepository.findByTenantIdAndEnabledTrue(tenantId);
    }
} 