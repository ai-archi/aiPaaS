package com.aixone.permission.annotation;

import java.lang.annotation.*;

/**
 * ABAC策略校验注解
 * 用于方法或类级别的属性访问控制
 * 
 * @author aixone
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAbac {
    
    /**
     * ABAC策略表达式
     * 支持用户属性、资源属性、环境属性的比较
     * 例如：user.department == resource.department AND user.level >= 3
     * 
     * @return 策略表达式
     */
    String expression();
    
    /**
     * 策略名称
     * 用于日志记录和调试
     * 
     * @return 策略名称
     */
    String name() default "";
    
    /**
     * 策略描述
     * 用于文档和调试
     * 
     * @return 策略描述
     */
    String description() default "";
    
    /**
     * 错误消息
     * 策略校验失败时返回的消息
     * 
     * @return 错误消息
     */
    String message() default "属性访问控制策略不满足";
    
    /**
     * 是否记录策略检查日志
     * 
     * @return 是否记录日志
     */
    boolean logAccess() default true;
    
    /**
     * 策略优先级
     * 数值越大优先级越高
     * 
     * @return 优先级
     */
    int priority() default 0;
}
