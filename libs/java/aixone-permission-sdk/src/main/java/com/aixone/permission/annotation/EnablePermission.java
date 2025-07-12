package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * 启用权限中台能力的注解
 * 标记在Spring Boot启动类上，自动装配相关组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnablePermission {
} 