package com.aixone.common.tools;

import java.lang.reflect.Field;

/**
 * Bean 拷贝工具类，支持简单属性拷贝
 */
public class BeanUtils {
    /**
     * 将 source 的同名属性拷贝到 target
     * 仅支持基本类型和 String，忽略异常
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) return;
        Class<?> srcClass = source.getClass();
        Class<?> tgtClass = target.getClass();
        for (Field srcField : srcClass.getDeclaredFields()) {
            try {
                srcField.setAccessible(true);
                Field tgtField = null;
                try {
                    tgtField = tgtClass.getDeclaredField(srcField.getName());
                } catch (NoSuchFieldException e) {
                    continue;
                }
                tgtField.setAccessible(true);
                if (tgtField.getType().equals(srcField.getType())) {
                    tgtField.set(target, srcField.get(source));
                }
            } catch (Exception ignore) {}
        }
    }
}