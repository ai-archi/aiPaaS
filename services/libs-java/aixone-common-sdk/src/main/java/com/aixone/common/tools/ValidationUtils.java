package com.aixone.common.tools;

import java.util.Arrays;
import java.util.List;

/**
 * 验证工具类，提供通用的验证方法
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class ValidationUtils {

    private ValidationUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 验证标识符名称格式
     * 只能包含字母、数字、下划线，且必须以字母开头
     * 
     * @param name 名称
     * @return 是否有效
     */
    public static boolean isValidIdentifier(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        return name.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
    }

    /**
     * 验证标识符名称格式（带错误消息）
     * 
     * @param name 名称
     * @param fieldName 字段名称
     * @return 错误消息，如果验证通过返回null
     */
    public static String validateIdentifier(String name, String fieldName) {
        if (StringUtils.isBlank(name)) {
            return fieldName + "不能为空";
        }
        if (!isValidIdentifier(name)) {
            return fieldName + "格式不正确，只能包含字母、数字、下划线，且必须以字母开头";
        }
        return null;
    }

    /**
     * 验证数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * 验证数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 验证字符串长度
     * 
     * @param str 字符串
     * @param maxLength 最大长度
     * @return 是否在长度范围内
     */
    public static boolean isValidLength(String str, int maxLength) {
        if (str == null) {
            return true;
        }
        return str.length() <= maxLength;
    }

    /**
     * 验证字符串长度范围
     * 
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在长度范围内
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证是否为正整数
     * 
     * @param value 数值
     * @return 是否为正整数
     */
    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }

    /**
     * 验证是否为非负整数
     * 
     * @param value 数值
     * @return 是否为非负整数
     */
    public static boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }

    /**
     * 验证精度和小数位数的关系
     * 
     * @param precision 精度
     * @param scale 小数位数
     * @return 是否有效
     */
    public static boolean isValidPrecisionAndScale(Integer precision, Integer scale) {
        if (precision == null || scale == null) {
            return true;
        }
        return scale <= precision;
    }

    /**
     * 验证枚举值
     * 
     * @param value 值
     * @param validValues 有效值列表
     * @return 是否有效
     */
    public static boolean isValidEnum(String value, String... validValues) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return Arrays.asList(validValues).contains(value.toUpperCase());
    }

    /**
     * 验证枚举值（忽略大小写）
     * 
     * @param value 值
     * @param validValues 有效值列表
     * @return 是否有效
     */
    public static boolean isValidEnumIgnoreCase(String value, String... validValues) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String upperValue = value.toUpperCase();
        for (String validValue : validValues) {
            if (validValue.toUpperCase().equals(upperValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * 验证手机号格式（中国大陆）
     * 
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证URL格式
     * 
     * @param url URL
     * @return 是否有效
     */
    public static boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        return url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    }

    /**
     * 验证IP地址格式
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    public static boolean isValidIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        return ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }
}