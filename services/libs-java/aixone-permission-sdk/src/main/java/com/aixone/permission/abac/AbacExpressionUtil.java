package com.aixone.permission.abac;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * ABAC表达式工具类
 * 用于解析和评估ABAC策略表达式
 * 
 * @author aixone
 */
@Slf4j
public class AbacExpressionUtil {
    
    // 支持的比较操作符
    private static final Pattern EQUALS_PATTERN = Pattern.compile("\\s*==\\s*");
    private static final Pattern NOT_EQUALS_PATTERN = Pattern.compile("\\s*!=\\s*");
    private static final Pattern GREATER_EQUAL_PATTERN = Pattern.compile("\\s*>=\\s*");
    private static final Pattern LESS_EQUAL_PATTERN = Pattern.compile("\\s*<=\\s*");
    private static final Pattern GREATER_PATTERN = Pattern.compile("\\s*>\\s*");
    private static final Pattern LESS_PATTERN = Pattern.compile("\\s*<\\s*");
    private static final Pattern IN_PATTERN = Pattern.compile("\\s+IN\\s+");
    private static final Pattern AND_PATTERN = Pattern.compile("\\s+AND\\s+");
    private static final Pattern OR_PATTERN = Pattern.compile("\\s+OR\\s+");
    
    /**
     * 评估ABAC表达式
     * 
     * @param expression 表达式
     * @param context 上下文
     * @return 评估结果
     */
    public boolean evaluate(String expression, Map<String, Object> context) {
        if (expression == null || expression.trim().isEmpty()) {
            return true;
        }
        
        try {
            // 预处理表达式
            String processedExpression = preprocessExpression(expression);
            
            // 评估表达式
            return evaluateExpression(processedExpression, context);
            
        } catch (Exception e) {
            log.error("表达式评估异常: {}", expression, e);
            return false;
        }
    }
    
    /**
     * 解析并评估ABAC策略表达式（静态方法，保持向后兼容）
     * 
     * @param expression 策略表达式，如 "user.dept == resource.dept && context.time in [9,18]"
     * @param userAttr 用户属性
     * @param resAttr 资源属性
     * @param context 上下文
     * @return 是否通过
     */
    public static boolean evaluate(String expression, Map<String, Object> userAttr, Map<String, Object> resAttr, Map<String, Object> context) {
        // 合并所有上下文
        Map<String, Object> fullContext = new java.util.HashMap<>();
        if (userAttr != null) {
            userAttr.forEach((key, value) -> fullContext.put("user." + key, value));
        }
        if (resAttr != null) {
            resAttr.forEach((key, value) -> fullContext.put("resource." + key, value));
        }
        if (context != null) {
            fullContext.putAll(context);
        }
        
        // 使用实例方法评估
        AbacExpressionUtil util = new AbacExpressionUtil();
        return util.evaluate(expression, fullContext);
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
    
    /**
     * 预处理表达式
     * 
     * @param expression 原始表达式
     * @return 处理后的表达式
     */
    private String preprocessExpression(String expression) {
        // 标准化空格
        String processed = expression.trim().replaceAll("\\s+", " ");
        
        // 标准化操作符
        processed = EQUALS_PATTERN.matcher(processed).replaceAll(" == ");
        processed = NOT_EQUALS_PATTERN.matcher(processed).replaceAll(" != ");
        processed = GREATER_EQUAL_PATTERN.matcher(processed).replaceAll(" >= ");
        processed = LESS_EQUAL_PATTERN.matcher(processed).replaceAll(" <= ");
        processed = GREATER_PATTERN.matcher(processed).replaceAll(" > ");
        processed = LESS_PATTERN.matcher(processed).replaceAll(" < ");
        processed = IN_PATTERN.matcher(processed).replaceAll(" IN ");
        processed = AND_PATTERN.matcher(processed).replaceAll(" AND ");
        processed = OR_PATTERN.matcher(processed).replaceAll(" OR ");
        
        return processed;
    }
    
    /**
     * 评估表达式
     * 
     * @param expression 表达式
     * @param context 上下文
     * @return 评估结果
     */
    private boolean evaluateExpression(String expression, Map<String, Object> context) {
        // 处理AND操作
        if (expression.contains(" AND ")) {
            String[] parts = expression.split(" AND ");
            for (String part : parts) {
                if (!evaluateExpression(part.trim(), context)) {
                    return false;
                }
            }
            return true;
        }
        
        // 处理OR操作
        if (expression.contains(" OR ")) {
            String[] parts = expression.split(" OR ");
            for (String part : parts) {
                if (evaluateExpression(part.trim(), context)) {
                    return true;
                }
            }
            return false;
        }
        
        // 处理比较操作
        return evaluateComparison(expression, context);
    }
    
    /**
     * 评估比较操作
     * 
     * @param expression 表达式
     * @param context 上下文
     * @return 比较结果
     */
    private boolean evaluateComparison(String expression, Map<String, Object> context) {
        // 处理 == 操作
        if (expression.contains(" == ")) {
            String[] parts = expression.split(" == ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, "==");
            }
        }
        
        // 处理 != 操作
        if (expression.contains(" != ")) {
            String[] parts = expression.split(" != ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, "!=");
            }
        }
        
        // 处理 >= 操作
        if (expression.contains(" >= ")) {
            String[] parts = expression.split(" >= ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, ">=");
            }
        }
        
        // 处理 <= 操作
        if (expression.contains(" <= ")) {
            String[] parts = expression.split(" <= ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, "<=");
            }
        }
        
        // 处理 > 操作
        if (expression.contains(" > ")) {
            String[] parts = expression.split(" > ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, ">");
            }
        }
        
        // 处理 < 操作
        if (expression.contains(" < ")) {
            String[] parts = expression.split(" < ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, "<");
            }
        }
        
        // 处理 IN 操作
        if (expression.contains(" IN ")) {
            String[] parts = expression.split(" IN ");
            if (parts.length == 2) {
                Object leftValue = getValue(parts[0].trim(), context);
                Object rightValue = getValue(parts[1].trim(), context);
                return compareValues(leftValue, rightValue, "IN");
            }
        }
        
        // 默认返回false
        return false;
    }
    
    /**
     * 获取值
     * 
     * @param key 键
     * @param context 上下文
     * @return 值
     */
    private Object getValue(String key, Map<String, Object> context) {
        // 如果是字符串字面量
        if (key.startsWith("'") && key.endsWith("'")) {
            return key.substring(1, key.length() - 1);
        }
        
        // 如果是数字
        if (key.matches("\\d+")) {
            return Integer.parseInt(key);
        }
        
        // 如果是浮点数
        if (key.matches("\\d+\\.\\d+")) {
            return Double.parseDouble(key);
        }
        
        // 从上下文中获取
        return context.get(key);
    }
    
    /**
     * 比较值
     * 
     * @param left 左值
     * @param right 右值
     * @param operator 操作符
     * @return 比较结果
     */
    private boolean compareValues(Object left, Object right, String operator) {
        if (left == null || right == null) {
            return false;
        }
        
        switch (operator) {
            case "==":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            case ">=":
                return compareNumbers(left, right) >= 0;
            case "<=":
                return compareNumbers(left, right) <= 0;
            case ">":
                return compareNumbers(left, right) > 0;
            case "<":
                return compareNumbers(left, right) < 0;
            case "IN":
                if (right instanceof String) {
                    String[] values = ((String) right).split(",");
                    for (String value : values) {
                        if (left.toString().trim().equals(value.trim())) {
                            return true;
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }
    
    /**
     * 比较数字
     * 
     * @param left 左值
     * @param right 右值
     * @return 比较结果
     */
    private int compareNumbers(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            double leftNum = ((Number) left).doubleValue();
            double rightNum = ((Number) right).doubleValue();
            return Double.compare(leftNum, rightNum);
        }
        
        // 尝试转换为数字
        try {
            double leftNum = Double.parseDouble(left.toString());
            double rightNum = Double.parseDouble(right.toString());
            return Double.compare(leftNum, rightNum);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
} 