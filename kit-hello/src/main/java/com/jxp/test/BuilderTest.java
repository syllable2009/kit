package com.jxp.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-08-12 15:32
 */

@Slf4j
public class BuilderTest {
    public static void main(String[] args) {

    }

    // 模拟从db获取
    private static BuilderClass getObj() {
        return BuilderClass.builder().build();
    }

    private Object instance(BuilderClass obj) {
        try {
            final Class<?> aClass = Class.forName(obj.getClazz());
            final Method method = findBuilderMethod(aClass, "builder");
            Object instance;
            if (null == method) {
                instance = aClass.getMethod("builder").invoke(null);
            } else {
                instance = aClass.getConstructor().newInstance();
            }
            final Method init = findBuilderMethod(aClass, "init", Map.class);
            if (null != init) {
                init.invoke(instance, obj.getConfig());
            }
            // 初始化赋值
            setParams(instance, obj.getFieldMap(), null);
        } catch (ClassNotFoundException e1) {
            log.error("instance error,obj:{}", obj);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                 | InvocationTargetException e2) {
            log.error("instance error,obj:{}", obj);
        }
        return null;
    }

    private static Method findBuilderMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
        try {
            // 查找静态builder()方法
            return clazz.getMethod(method, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static void setParams(Object obj,
            Map<String, String> fieldMap, Object config) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                String fieldName = field.getName();
                String methodName = fieldMap.getOrDefault(fieldName, null);
                if (StrUtil.isBlank(methodName)) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(config);
                Method method = obj.getClass().getDeclaredMethod(methodName, field.getType());
                method.invoke(obj, value);
            } catch (NoSuchMethodException e) {
                // 忽略
            } catch (Exception e) {
                log.error("setParams error", e);
            }
        }
    }
}
