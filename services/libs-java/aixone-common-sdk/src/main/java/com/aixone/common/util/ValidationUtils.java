package com.aixone.common.util;

import com.aixone.common.exception.ValidationException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class ValidationUtils {
    
    /** 邮箱正则表达式 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    /** 手机号正则表达式（中国大陆） */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    /** 身份证号正则表达式（中国大陆） */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"
    );
    
    /**
     * 检查对象是否为空
     * 
     * @param obj 对象
     * @param message 错误消息
     * @throws ValidationException 如果对象为空
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查字符串是否为空
     * 
     * @param str 字符串
     * @param message 错误消息
     * @throws ValidationException 如果字符串为空
     */
    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查字符串是否不为空白
     * 
     * @param str 字符串
     * @param message 错误消息
     * @throws ValidationException 如果字符串为空白
     */
    public static void notBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查集合是否为空
     * 
     * @param collection 集合
     * @param message 错误消息
     * @throws ValidationException 如果集合为空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查Map是否为空
     * 
     * @param map Map
     * @param message 错误消息
     * @throws ValidationException 如果Map为空
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查条件是否为真
     * 
     * @param condition 条件
     * @param message 错误消息
     * @throws ValidationException 如果条件为假
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查条件是否为假
     * 
     * @param condition 条件
     * @param message 错误消息
     * @throws ValidationException 如果条件为真
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查字符串长度
     * 
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @param message 错误消息
     * @throws ValidationException 如果长度不符合要求
     */
    public static void checkLength(String str, int minLength, int maxLength, String message) {
        if (str == null) {
            throw new ValidationException(message);
        }
        int length = str.length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查邮箱格式
     * 
     * @param email 邮箱
     * @param message 错误消息
     * @throws ValidationException 如果邮箱格式不正确
     */
    public static void checkEmail(String email, String message) {
        if (StringUtils.isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查手机号格式
     * 
     * @param phone 手机号
     * @param message 错误消息
     * @throws ValidationException 如果手机号格式不正确
     */
    public static void checkPhone(String phone, String message) {
        if (StringUtils.isBlank(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查身份证号格式
     * 
     * @param idCard 身份证号
     * @param message 错误消息
     * @throws ValidationException 如果身份证号格式不正确
     */
    public static void checkIdCard(String idCard, String message) {
        if (StringUtils.isBlank(idCard) || !ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @param message 错误消息
     * @throws ValidationException 如果数值超出范围
     */
    public static void checkRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @param message 错误消息
     * @throws ValidationException 如果数值超出范围
     */
    public static void checkRange(long value, long min, long max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * 检查数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @param message 错误消息
     * @throws ValidationException 如果数值超出范围
     */
    public static void checkRange(double value, double min, double max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
}
