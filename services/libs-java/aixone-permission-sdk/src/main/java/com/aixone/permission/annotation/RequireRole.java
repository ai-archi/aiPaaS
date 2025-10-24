package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * 角色校验注解
 * 用于方法或类级别的角色控制
 * 
 * @author aixone
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    
    /**
     * 角色名称
     * 支持多个角色，用逗号分隔
     * 
     * @return 角色名称
     */
    String value();
    
    /**
     * 角色匹配模式
     * ALL: 需要拥有所有角色
     * ANY: 拥有任意一个角色即可
     * 
     * @return 匹配模式
     */
    Mode mode() default Mode.ANY;
    
    /**
     * 错误消息
     * 角色校验失败时返回的消息
     * 
     * @return 错误消息
     */
    String message() default "角色权限不足";
    
    /**
     * 是否记录角色检查日志
     * 
     * @return 是否记录日志
     */
    boolean logAccess() default true;
    
    /**
     * 角色匹配模式枚举
     */
    enum Mode {
        /**
         * 需要拥有所有角色
         */
        ALL,
        
        /**
         * 拥有任意一个角色即可
         */
        ANY
    }
}
