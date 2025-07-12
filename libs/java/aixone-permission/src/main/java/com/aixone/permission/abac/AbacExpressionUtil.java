package com.aixone.permission.abac;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ABAC表达式工具类
 * 用于解析和评估ABAC策略表达式
 * 
 * @author aixone
 */
@Slf4j
public class AbacExpressionUtil {
    
    /**
     * 解析并评估ABAC策略表达式
     * 
     * @param expression 策略表达式，如 "user.dept == resource.dept && context.time in [9,18]"
     * @param userAttr 用户属性
     * @param resAttr 资源属性
     * @param context 上下文
     * @return 是否通过
     */
    public static boolean evaluate(String expression, Map<String, Object> userAttr, Map<String, Object> resAttr, Map<String, Object> context) {
        // TODO: 可用SpEL/MVEL/Groovy等表达式引擎实现
        // 这里只做占位，实际可扩展
        log.debug("评估ABAC表达式: {}", expression);
        return true;
    }
    
    /**
     * 评估ABAC权限（简化版本）
     * 
     * @param userAttr 用户属性
     * @param resAttr 资源属性
     * @param context 上下文
     * @return 是否通过
     */
    public static boolean evaluate(Map<String, Object> userAttr, Map<String, Object> resAttr, Map<String, Object> context) {
        if (userAttr == null || resAttr == null) {
            log.warn("用户属性或资源属性为空，ABAC校验失败");
            return false;
        }
        
        // 简化实现：检查用户部门是否与资源部门相同
        String userDept = (String) userAttr.get("department");
        String resDept = (String) resAttr.get("department");
        
        if (userDept != null && userDept.equals(resDept)) {
            log.debug("用户部门 {} 与资源部门 {} 匹配，ABAC校验通过", userDept, resDept);
            return true;
        }
        
        log.debug("用户部门 {} 与资源部门 {} 不匹配，ABAC校验失败", userDept, resDept);
        return false;
    }
} 