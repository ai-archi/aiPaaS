package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ABAC权限决策引擎
 * 基于属性的访问控制（Attribute-Based Access Control）
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AbacDecisionEngine {

    /**
     * 检查用户是否有指定权限（通过ABAC条件）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param permission 权限对象（包含ABAC条件）
     * @param userAttributes 用户ABAC属性
     * @param resourceAttributes 资源ABAC属性（可选）
     * @param environmentAttributes 环境ABAC属性（可选）
     * @return 是否有权限
     */
    public boolean checkPermission(
            String userId,
            String tenantId,
            Permission permission,
            Map<String, Object> userAttributes,
            Map<String, Object> resourceAttributes,
            Map<String, Object> environmentAttributes) {
        
        log.debug("ABAC权限检查: userId={}, tenantId={}, permission={}, userAttributes={}", 
                userId, tenantId, permission.getPermissionIdentifier(), userAttributes);

        // 如果权限没有ABAC条件，默认通过（由RBAC决策）
        if (!permission.hasAbacConditions()) {
            log.debug("权限没有ABAC条件，跳过ABAC检查");
            return true;
        }

        Map<String, Object> abacConditions = permission.getAbacConditions();
        if (abacConditions == null || abacConditions.isEmpty()) {
            log.debug("ABAC条件为空，跳过ABAC检查");
            return true;
        }

        // 评估ABAC条件
        return evaluateAbacConditions(abacConditions, userAttributes, resourceAttributes, environmentAttributes);
    }

    /**
     * 评估ABAC条件
     * 支持简单的条件表达式：
     * - 等于：{"user.department": "IT"}
     * - 不等于：{"user.department": {"$ne": "HR"}}
     * - 包含：{"user.roles": {"$in": ["admin", "manager"]}}
     * - 大于/小于：{"user.level": {"$gt": 5}}
     * 
     * @param conditions ABAC条件
     * @param userAttributes 用户属性
     * @param resourceAttributes 资源属性
     * @param environmentAttributes 环境属性
     * @return 是否满足条件
     */
    private boolean evaluateAbacConditions(
            Map<String, Object> conditions,
            Map<String, Object> userAttributes,
            Map<String, Object> resourceAttributes,
            Map<String, Object> environmentAttributes) {
        
        // 合并所有属性（优先级：用户属性 > 资源属性 > 环境属性）
        Map<String, Object> allAttributes = new java.util.HashMap<>();
        if (environmentAttributes != null) {
            allAttributes.putAll(environmentAttributes);
        }
        if (resourceAttributes != null) {
            allAttributes.putAll(resourceAttributes);
        }
        if (userAttributes != null) {
            allAttributes.putAll(userAttributes);
        }

        // 评估每个条件
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();

            // 获取实际值（支持嵌套属性，如 user.department）
            Object actualValue = getNestedValue(allAttributes, key);

            // 评估条件
            if (!evaluateCondition(actualValue, expectedValue)) {
                log.debug("ABAC条件不满足: key={}, expected={}, actual={}", key, expectedValue, actualValue);
                return false;
            }
        }

        log.debug("所有ABAC条件都满足");
        return true;
    }

    /**
     * 获取嵌套属性值
     * 支持点号分隔的属性路径，如 "user.department"
     */
    private Object getNestedValue(Map<String, Object> attributes, String key) {
        if (attributes == null || key == null) {
            return null;
        }

        // 如果key包含点号，需要递归查找
        if (key.contains(".")) {
            String[] parts = key.split("\\.", 2);
            Object parentValue = attributes.get(parts[0]);
            if (parentValue instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> parentMap = (Map<String, Object>) parentValue;
                return getNestedValue(parentMap, parts[1]);
            }
            return null;
        }

        return attributes.get(key);
    }

    /**
     * 评估单个条件
     */
    @SuppressWarnings("unchecked")
    private boolean evaluateCondition(Object actualValue, Object expectedValue) {
        if (actualValue == null && expectedValue == null) {
            return true;
        }
        if (actualValue == null || expectedValue == null) {
            return false;
        }

        // 如果expectedValue是Map，可能是操作符（如 $ne, $in, $gt等）
        if (expectedValue instanceof Map) {
            Map<String, Object> operators = (Map<String, Object>) expectedValue;
            
            // $ne: 不等于
            if (operators.containsKey("$ne")) {
                return !actualValue.equals(operators.get("$ne"));
            }
            
            // $in: 包含在列表中
            if (operators.containsKey("$in")) {
                Object inValue = operators.get("$in");
                if (inValue instanceof java.util.List) {
                    return ((java.util.List<?>) inValue).contains(actualValue);
                }
            }
            
            // $gt: 大于
            if (operators.containsKey("$gt")) {
                return compareNumbers(actualValue, operators.get("$gt")) > 0;
            }
            
            // $gte: 大于等于
            if (operators.containsKey("$gte")) {
                return compareNumbers(actualValue, operators.get("$gte")) >= 0;
            }
            
            // $lt: 小于
            if (operators.containsKey("$lt")) {
                return compareNumbers(actualValue, operators.get("$lt")) < 0;
            }
            
            // $lte: 小于等于
            if (operators.containsKey("$lte")) {
                return compareNumbers(actualValue, operators.get("$lte")) <= 0;
            }
        }

        // 默认：相等比较
        return actualValue.equals(expectedValue);
    }

    /**
     * 比较数字
     */
    private int compareNumbers(Object actual, Object expected) {
        if (actual instanceof Number && expected instanceof Number) {
            double actualNum = ((Number) actual).doubleValue();
            double expectedNum = ((Number) expected).doubleValue();
            return Double.compare(actualNum, expectedNum);
        }
        return 0;
    }
}

