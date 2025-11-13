package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.ValueObject;

/**
 * 事件类型值对象
 * 定义事件的类型和分类
 */
public class EventType extends ValueObject {
    private final String type;
    private final String category;
    private final String version;
    
    public EventType(String type, String category, String version) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Event type cannot be null or empty");
        }
        this.type = type.trim();
        this.category = category != null ? category.trim() : "default";
        this.version = version != null ? version.trim() : "1.0";
    }
    
    public EventType(String type) {
        this(type, "default", "1.0");
    }
    
    public String getType() {
        return type;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getFullType() {
        return category + "." + type + "." + version;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventType eventType = (EventType) obj;
        return type.equals(eventType.type) &&
               category.equals(eventType.category) &&
               version.equals(eventType.version);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, category, version);
    }
    
    @Override
    public String toString() {
        return getFullType();
    }
}
