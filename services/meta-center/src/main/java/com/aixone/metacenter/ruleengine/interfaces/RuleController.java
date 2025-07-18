package com.aixone.metacenter.ruleengine.interfaces;

import com.aixone.metacenter.ruleengine.application.RuleApplicationService;
import com.aixone.metacenter.ruleengine.domain.Rule;
import com.aixone.metacenter.common.constant.MetaConstants;
import com.aixone.metacenter.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 规则引擎控制器
 * 提供规则管理的REST API接口
 */
@Slf4j
@RestController
@RequestMapping(MetaConstants.Api.API_PREFIX + "/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleApplicationService ruleApplicationService;

    /**
     * 创建规则
     *
     * @param rule 规则对象
     * @return 创建的规则
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Rule>> createRule(@Valid @RequestBody Rule rule) {
        try {
            log.info("创建规则: {}", rule.getName());
            Rule createdRule = ruleApplicationService.createRule(rule);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdRule, "规则创建成功"));
        } catch (Exception e) {
            log.error("创建规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_CREATE_ERROR", "创建规则失败: " + e.getMessage()));
        }
    }

    /**
     * 更新规则
     *
     * @param id 规则ID
     * @param rule 规则对象
     * @return 更新后的规则
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Rule>> updateRule(@PathVariable Long id, @Valid @RequestBody Rule rule) {
        try {
            log.info("更新规则: {}", id);
            Rule updatedRule = ruleApplicationService.updateRule(id, rule);
            return ResponseEntity.ok(ApiResponse.success(updatedRule, "规则更新成功"));
        } catch (Exception e) {
            log.error("更新规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_UPDATE_ERROR", "更新规则失败: " + e.getMessage()));
        }
    }

    /**
     * 删除规则
     *
     * @param id 规则ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        try {
            log.info("删除规则: {}", id);
            ruleApplicationService.deleteRule(id);
            return ResponseEntity.ok(ApiResponse.success(null, "规则删除成功"));
        } catch (Exception e) {
            log.error("删除规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_DELETE_ERROR", "删除规则失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询规则
     *
     * @param id 规则ID
     * @return 规则
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rule>> getRuleById(@PathVariable Long id) {
        try {
            log.debug("查询规则: {}", id);
            Rule rule = ruleApplicationService.getRuleById(id);
            return ResponseEntity.ok(ApiResponse.success(rule, "规则查询成功"));
        } catch (Exception e) {
            log.error("查询规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_QUERY_ERROR", "查询规则失败: " + e.getMessage()));
        }
    }

    /**
     * 分页查询规则
     *
     * @param page 页码
     * @param size 页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Rule>>> getRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.debug("分页查询规则: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Rule> rules = ruleApplicationService.getRules(pageable);
            return ResponseEntity.ok(ApiResponse.success(rules, "规则分页查询成功"));
        } catch (Exception e) {
            log.error("分页查询规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_QUERY_ERROR", "分页查询规则失败: " + e.getMessage()));
        }
    }

    /**
     * 启用规则
     *
     * @param id 规则ID
     * @return 启用结果
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<Rule>> enableRule(@PathVariable Long id) {
        try {
            log.info("启用规则: {}", id);
            Rule rule = ruleApplicationService.enableRule(id);
            return ResponseEntity.ok(ApiResponse.success(rule, "规则启用成功"));
        } catch (Exception e) {
            log.error("启用规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_ENABLE_ERROR", "启用规则失败: " + e.getMessage()));
        }
    }

    /**
     * 禁用规则
     *
     * @param id 规则ID
     * @return 禁用结果
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<Rule>> disableRule(@PathVariable Long id) {
        try {
            log.info("禁用规则: {}", id);
            Rule rule = ruleApplicationService.disableRule(id);
            return ResponseEntity.ok(ApiResponse.success(rule, "规则禁用成功"));
        } catch (Exception e) {
            log.error("禁用规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_DISABLE_ERROR", "禁用规则失败: " + e.getMessage()));
        }
    }

    /**
     * 执行规则
     *
     * @param id 规则ID
     * @param data 规则执行数据
     * @return 执行结果
     */
    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<Object>> executeRule(@PathVariable Long id, @RequestBody Object data) {
        try {
            log.info("执行规则: {}", id);
            Object result = ruleApplicationService.executeRule(id, data);
            return ResponseEntity.ok(ApiResponse.success(result, "规则执行成功"));
        } catch (Exception e) {
            log.error("执行规则失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_EXECUTE_ERROR", "执行规则失败: " + e.getMessage()));
        }
    }

    /**
     * 验证规则表达式
     *
     * @param expression 规则表达式
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateRuleExpression(@RequestBody String expression) {
        try {
            log.info("验证规则表达式: {}", expression);
            boolean isValid = ruleApplicationService.validateRuleExpression(expression);
            return ResponseEntity.ok(ApiResponse.success(isValid, "规则表达式验证完成"));
        } catch (Exception e) {
            log.error("验证规则表达式失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_VALIDATE_ERROR", "验证规则表达式失败: " + e.getMessage()));
        }
    }

    /**
     * 根据租户ID查询规则列表
     *
     * @param tenantId 租户ID
     * @return 规则列表
     */
    @GetMapping("/by-tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<Rule>>> getRulesByTenantId(@PathVariable String tenantId) {
        try {
            log.debug("根据租户ID查询规则列表: {}", tenantId);
            List<Rule> rules = ruleApplicationService.getRulesByTenantId(tenantId);
            return ResponseEntity.ok(ApiResponse.success(rules, "租户规则查询成功"));
        } catch (Exception e) {
            log.error("根据租户ID查询规则列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RULE_QUERY_ERROR", "根据租户ID查询规则列表失败: " + e.getMessage()));
        }
    }
} 