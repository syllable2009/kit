package com.jxp.system.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-12 15:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Argument {
    String key(); // Argument key

    String description(); // Argument description

    boolean optional() default false; // Whether the argument is optional

    // 是否为必须的，非必须的有默认值
    String value() default "";
}
