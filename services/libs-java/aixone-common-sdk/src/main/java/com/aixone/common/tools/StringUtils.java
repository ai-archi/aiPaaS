package com.aixone.common.tools;

/**
 * 字符串工具类，提供常用字符串处理方法
 */
public class StringUtils {
    /** 判断字符串是否为空（null或全空白） */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    /** 判断字符串是否不为空 */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    /** 去除字符串首尾空白，null安全 */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    /** 首字母大写 */
    public static String capitalize(String str) {
        if (isBlank(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}