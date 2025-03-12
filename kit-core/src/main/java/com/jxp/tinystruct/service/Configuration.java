package com.jxp.tinystruct.service;

import java.io.Serializable;
import java.util.Set;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 17:44
 */
public interface Configuration<T> extends Serializable {
    void set(String name, T value);

    T get(String name);

    void remove(String name);

    String toString();

    Set<String> propertyNames();

    T getOrDefault(T s, T value);

    void setIfAbsent(T s, T path);
}
