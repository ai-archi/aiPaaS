package com.aixone.common.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * 集合工具类，提供常用集合处理方法
 */
public class CollectionUtils {
    /** 判断集合是否为空 */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
    /** 判断集合是否不为空 */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }
    /** 合并两个集合为新List */
    public static <T> List<T> merge(Collection<T> a, Collection<T> b) {
        List<T> result = new ArrayList<>();
        if (a != null) result.addAll(a);
        if (b != null) result.addAll(b);
        return result;
    }
    /** 集合去重 */
    public static <T> List<T> distinct(Collection<T> coll) {
        if (coll == null) return null;
        Set<T> set = new HashSet<>(coll);
        return new ArrayList<>(set);
    }
}