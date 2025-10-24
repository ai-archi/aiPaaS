package com.aixone.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期时间工具类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class DateUtils {
    
    /** 默认日期时间格式 */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    /** 默认时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);
    
    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * 格式化当前时间为字符串
     * 
     * @return 格式化后的时间字符串
     */
    public static String formatNow() {
        return format(now());
    }
    
    /**
     * 格式化时间为字符串（使用默认格式）
     * 
     * @param dateTime 时间
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 格式化时间为字符串（使用指定格式）
     * 
     * @param dateTime 时间
     * @param pattern 格式
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析时间字符串
     * 
     * @param dateTimeStr 时间字符串
     * @return 解析后的时间
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }
    
    /**
     * 解析时间字符串（使用指定格式）
     * 
     * @param dateTimeStr 时间字符串
     * @param pattern 格式
     * @return 解析后的时间
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 转换为Date对象
     * 
     * @param dateTime 时间
     * @return Date对象
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 从Date对象转换
     * 
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime fromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
