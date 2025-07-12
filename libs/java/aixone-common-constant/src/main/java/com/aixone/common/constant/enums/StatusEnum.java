package com.aixone.common.constant.enums;

/**
 * 通用启用/禁用状态枚举
 */
public enum StatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int getCode() { return code; }
    public String getDesc() { return desc; }
} 