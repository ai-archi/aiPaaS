package com.aixone.common.constant.enums;

/**
 * 通用布尔枚举
 */
public enum YesNoEnum {
    NO(0, "否"),
    YES(1, "是");

    private final int code;
    private final String desc;

    YesNoEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int getCode() { return code; }
    public String getDesc() { return desc; }
} 