package com.aixone.common.ddd;

/**
 * 值对象基类
 * 值对象是不可变的，通过值而不是标识来比较
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public abstract class ValueObject {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
