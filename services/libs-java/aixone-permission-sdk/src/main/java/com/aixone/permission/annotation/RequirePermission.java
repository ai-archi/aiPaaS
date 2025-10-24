package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 用于方法或类级别的权限控制
 * 
 * @author aixone
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 权限标识
     * 格式：resource:action 或 权限ID
     * 
     * @return 权限标识
     */
    String value();
    
    /**
     * 资源标识
     * 当value不包含resource时使用
     * 
     * @return 资源标识
     */
    String resource() default "";
    
    /**
     * 操作类型
     * 当value不包含action时使用
     * 
     * @return 操作类型
     */
    String action() default "";
    
    /**
     * 权限级别
     * 用于更细粒度的权限控制
     * 
     * @return 权限级别
     */
    String level() default "";
    
    /**
     * 是否启用ABAC检查
     * 默认只进行RBAC检查
     * 
     * @return 是否启用ABAC
     */
    boolean enableAbac() default false;
    
    /**
     * 错误消息
     * 权限校验失败时返回的消息
     * 
     * @return 错误消息
     */
    String message() default "权限不足";
    
    /**
     * 是否记录权限检查日志
     * 
     * @return 是否记录日志
     */
    boolean logAccess() default true;
} 