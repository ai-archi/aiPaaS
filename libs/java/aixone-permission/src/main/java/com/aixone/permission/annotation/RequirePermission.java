package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * 方法/类级权限校验注解
 * 用于声明访问某资源所需的权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /** 权限标识，如"user:read" */
    String value();
    /** 操作类型，如"read"、"write"等，可选 */
    String action() default "";
} 