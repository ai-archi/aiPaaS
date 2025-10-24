package com.aixone.common.session;

import java.util.HashMap;
import java.util.Map;

/**
 * ABAC（属性基）权限相关属性对象
 */
public class AbacAttributes {
    private final Map<String, Object> attributes = new HashMap<>();

    public void put(String key, Object value) {
        attributes.put(key, value);
    }
    public Object get(String key) {
        return attributes.get(key);
    }
    public Map<String, Object> asMap() {
        return attributes;
    }
}
