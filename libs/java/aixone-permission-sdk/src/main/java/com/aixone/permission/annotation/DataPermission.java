package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于声明方法/类的数据权限处理类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /** 数据类型，如"department"、"project"等 */
    String dataType();
    /** 处理器bean名称，支持自定义扩展 */
    String handler() default "";
} 