package com.aixone.common.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据类型工具类，提供数据类型相关的工具方法
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class DataTypeUtils {

    private DataTypeUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 支持的数据类型
     */
    public static final String[] SUPPORTED_DATA_TYPES = {
        "STRING", "INTEGER", "LONG", "DOUBLE", "DECIMAL", "BOOLEAN", 
        "DATE", "DATETIME", "TIMESTAMP", "TEXT", "BLOB", "JSON"
    };

    /**
     * 数据类型默认值映射
     */
    private static final Map<String, Object> DEFAULT_VALUES = new HashMap<>();
    
    static {
        DEFAULT_VALUES.put("STRING", "");
        DEFAULT_VALUES.put("TEXT", "");
        DEFAULT_VALUES.put("INTEGER", 0);
        DEFAULT_VALUES.put("LONG", 0L);
        DEFAULT_VALUES.put("DOUBLE", 0.0);
        DEFAULT_VALUES.put("DECIMAL", 0.0);
        DEFAULT_VALUES.put("BOOLEAN", false);
        DEFAULT_VALUES.put("DATE", null);
        DEFAULT_VALUES.put("DATETIME", null);
        DEFAULT_VALUES.put("TIMESTAMP", null);
        DEFAULT_VALUES.put("JSON", "{}");
        DEFAULT_VALUES.put("BLOB", null);
    }

    /**
     * 验证数据类型是否支持
     * 
     * @param dataType 数据类型
     * @return 是否支持
     */
    public static boolean isSupportedDataType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return false;
        }
        return Arrays.asList(SUPPORTED_DATA_TYPES).contains(dataType.toUpperCase());
    }

    /**
     * 验证数据类型是否支持（带错误消息）
     * 
     * @param dataType 数据类型
     * @return 错误消息，如果验证通过返回null
     */
    public static String validateDataType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return "数据类型不能为空";
        }
        if (!isSupportedDataType(dataType)) {
            return "不支持的数据类型: " + dataType;
        }
        return null;
    }

    /**
     * 获取数据类型的默认值
     * 
     * @param dataType 数据类型
     * @return 默认值
     */
    public static Object getDefaultValue(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return null;
        }
        return DEFAULT_VALUES.get(dataType.toUpperCase());
    }

    /**
     * 验证值是否符合数据类型
     * 
     * @param dataType 数据类型
     * @param value 值
     * @return 是否符合
     */
    public static boolean isValidValue(String dataType, Object value) {
        if (value == null) {
            return true; // null值总是有效的
        }
        
        try {
            switch (dataType.toUpperCase()) {
                case "STRING":
                case "TEXT":
                    return value instanceof String;
                case "INTEGER":
                    if (value instanceof Integer) return true;
                    if (value instanceof String) {
                        Integer.parseInt((String) value);
                        return true;
                    }
                    return false;
                case "LONG":
                    if (value instanceof Long) return true;
                    if (value instanceof Integer) return true;
                    if (value instanceof String) {
                        Long.parseLong((String) value);
                        return true;
                    }
                    return false;
                case "DOUBLE":
                case "DECIMAL":
                    if (value instanceof Double) return true;
                    if (value instanceof Float) return true;
                    if (value instanceof Integer) return true;
                    if (value instanceof Long) return true;
                    if (value instanceof String) {
                        Double.parseDouble((String) value);
                        return true;
                    }
                    return false;
                case "BOOLEAN":
                    if (value instanceof Boolean) return true;
                    if (value instanceof String) {
                        String str = (String) value;
                        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
                    }
                    return false;
                case "DATE":
                case "DATETIME":
                case "TIMESTAMP":
                    // TODO: 实现日期格式验证
                    return true;
                case "JSON":
                    if (value instanceof String) {
                        String str = (String) value;
                        return str.startsWith("{") && str.endsWith("}") ||
                               str.startsWith("[") && str.endsWith("]");
                    }
                    return value instanceof Map || value instanceof java.util.List;
                case "BLOB":
                    return value instanceof byte[];
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证值是否符合数据类型（带长度限制）
     * 
     * @param dataType 数据类型
     * @param value 值
     * @param maxLength 最大长度
     * @return 是否符合
     */
    public static boolean isValidValue(String dataType, Object value, Integer maxLength) {
        if (!isValidValue(dataType, value)) {
            return false;
        }
        
        if (maxLength != null && value instanceof String) {
            String str = (String) value;
            return str.length() <= maxLength;
        }
        
        return true;
    }

    /**
     * 转换值为指定数据类型
     * 
     * @param dataType 数据类型
     * @param value 值
     * @return 转换后的值
     */
    public static Object convertValue(String dataType, Object value) {
        if (value == null) {
            return getDefaultValue(dataType);
        }
        
        try {
            switch (dataType.toUpperCase()) {
                case "STRING":
                case "TEXT":
                    return String.valueOf(value);
                case "INTEGER":
                    if (value instanceof Integer) return value;
                    if (value instanceof String) return Integer.parseInt((String) value);
                    if (value instanceof Long) return ((Long) value).intValue();
                    if (value instanceof Double) return ((Double) value).intValue();
                    return Integer.parseInt(String.valueOf(value));
                case "LONG":
                    if (value instanceof Long) return value;
                    if (value instanceof Integer) return ((Integer) value).longValue();
                    if (value instanceof String) return Long.parseLong((String) value);
                    if (value instanceof Double) return ((Double) value).longValue();
                    return Long.parseLong(String.valueOf(value));
                case "DOUBLE":
                case "DECIMAL":
                    if (value instanceof Double) return value;
                    if (value instanceof Float) return ((Float) value).doubleValue();
                    if (value instanceof Integer) return ((Integer) value).doubleValue();
                    if (value instanceof Long) return ((Long) value).doubleValue();
                    if (value instanceof String) return Double.parseDouble((String) value);
                    return Double.parseDouble(String.valueOf(value));
                case "BOOLEAN":
                    if (value instanceof Boolean) return value;
                    if (value instanceof String) {
                        String str = (String) value;
                        return "true".equalsIgnoreCase(str) || "1".equals(str);
                    }
                    if (value instanceof Integer) return ((Integer) value) != 0;
                    if (value instanceof Long) return ((Long) value) != 0L;
                    return Boolean.parseBoolean(String.valueOf(value));
                case "DATE":
                case "DATETIME":
                case "TIMESTAMP":
                    // TODO: 实现日期转换
                    return value;
                case "JSON":
                    if (value instanceof String) return value;
                    if (value instanceof Map || value instanceof java.util.List) return value;
                    return String.valueOf(value);
                case "BLOB":
                    if (value instanceof byte[]) return value;
                    if (value instanceof String) return ((String) value).getBytes();
                    return String.valueOf(value).getBytes();
                default:
                    return value;
            }
        } catch (Exception e) {
            return getDefaultValue(dataType);
        }
    }

    /**
     * 获取数据类型的Java类型
     * 
     * @param dataType 数据类型
     * @return Java类型
     */
    public static Class<?> getJavaType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return Object.class;
        }
        
        switch (dataType.toUpperCase()) {
            case "STRING":
            case "TEXT":
                return String.class;
            case "INTEGER":
                return Integer.class;
            case "LONG":
                return Long.class;
            case "DOUBLE":
            case "DECIMAL":
                return Double.class;
            case "BOOLEAN":
                return Boolean.class;
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                return java.time.LocalDateTime.class;
            case "JSON":
                return String.class;
            case "BLOB":
                return byte[].class;
            default:
                return Object.class;
        }
    }

    /**
     * 判断数据类型是否为数值类型
     * 
     * @param dataType 数据类型
     * @return 是否为数值类型
     */
    public static boolean isNumericType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return false;
        }
        
        String upperType = dataType.toUpperCase();
        return "INTEGER".equals(upperType) || 
               "LONG".equals(upperType) || 
               "DOUBLE".equals(upperType) || 
               "DECIMAL".equals(upperType);
    }

    /**
     * 判断数据类型是否为字符串类型
     * 
     * @param dataType 数据类型
     * @return 是否为字符串类型
     */
    public static boolean isStringType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return false;
        }
        
        String upperType = dataType.toUpperCase();
        return "STRING".equals(upperType) || "TEXT".equals(upperType);
    }

    /**
     * 判断数据类型是否为日期类型
     * 
     * @param dataType 数据类型
     * @return 是否为日期类型
     */
    public static boolean isDateType(String dataType) {
        if (StringUtils.isBlank(dataType)) {
            return false;
        }
        
        String upperType = dataType.toUpperCase();
        return "DATE".equals(upperType) || 
               "DATETIME".equals(upperType) || 
               "TIMESTAMP".equals(upperType);
    }
} 