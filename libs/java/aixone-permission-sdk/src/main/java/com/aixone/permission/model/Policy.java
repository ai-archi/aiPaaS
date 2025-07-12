package com.aixone.permission.model;

/**
 * 策略模型
 * 代表ABAC等复杂权限策略
 */
public class Policy {
    /** 策略表达式，如user.dept == resource.dept */
    private String expression;

    public Policy() {}
    public Policy(String expression) {
        this.expression = expression;
    }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
} 