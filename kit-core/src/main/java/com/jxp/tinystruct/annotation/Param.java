package com.jxp.tinystruct.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 10:50
 */
// Argument annotation for specifying argument details
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Param {

    String value(); // Argument key

    String description() default ""; // Argument description

    boolean optional() default false; // Whether the argument is optional

    // 是否为必须的，非必须的有默认值
    String init() default "";
}
