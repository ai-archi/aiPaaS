package com.aixone.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public class JsonUtils {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        // 注册Java时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    
    /**
     * 对象转JSON字符串
     * 
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }
    
    /**
     * JSON字符串转对象
     * 
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }
    
    /**
     * JSON字符串转对象（支持泛型）
     * 
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }
    
    /**
     * JSON字符串转List
     * 
     * @param json JSON字符串
     * @param clazz 元素类型
     * @param <T> 泛型类型
     * @return List对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转List失败", e);
        }
    }
    
    /**
     * JSON字符串转Map
     * 
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转Map失败", e);
        }
    }
    
    /**
     * 对象转Map
     * 
     * @param obj 对象
     * @return Map对象
     */
    public static Map<String, Object> toMap(Object obj) {
        return OBJECT_MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Map转对象
     * 
     * @param map Map对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 对象
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(map, clazz);
    }
    
    /**
     * 对象深拷贝
     * 
     * @param obj 原对象
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 拷贝后的对象
     */
    public static <T> T deepCopy(Object obj, Class<T> clazz) {
        String json = toJson(obj);
        return fromJson(json, clazz);
    }
    
    /**
     * 对象深拷贝（使用TypeReference）
     * 
     * @param obj 原对象
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 拷贝后的对象
     */
    public static <T> T deepCopy(Object obj, TypeReference<T> typeReference) {
        String json = toJson(obj);
        return fromJson(json, typeReference);
    }
    
    /**
     * 检查字符串是否为有效JSON
     * 
     * @param json JSON字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    /**
     * 格式化JSON字符串
     * 
     * @param json JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Object obj = OBJECT_MAPPER.readValue(json, Object.class);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON格式化失败", e);
        }
    }
}
