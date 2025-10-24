package com.aixone.common.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类，提供常用日期处理方法
 */
public class DateUtils {
    /** 默认日期格式 */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 获取当前时间 */
    public static Date now() {
        return new Date();
    }
    /** 按指定格式格式化日期 */
    public static String format(Date date, String pattern) {
        if (date == null) return null;
        return new SimpleDateFormat(pattern).format(date);
    }
    /** 按默认格式格式化日期 */
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }
    /** 字符串转日期 */
    public static Date parse(String str, String pattern) throws ParseException {
        if (str == null) return null;
        return new SimpleDateFormat(pattern).parse(str);
    }
    /** 字符串转日期（默认格式） */
    public static Date parse(String str) throws ParseException {
        return parse(str, DEFAULT_PATTERN);
    }
}