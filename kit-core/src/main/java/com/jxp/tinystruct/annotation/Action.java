package com.jxp.tinystruct.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 10:48
 */
// Action annotation for CLI and web applications
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD}) // 指定注解的目标
public @interface Action {
    String value(); // The URI path or command name

    String description() default ""; // Description of the action

    // 参数应该严格匹配方法的参数，否则会出错
    Param[] params() default {}; // Arguments expected by the action

    Param[] options() default {}; // Command-line options

    String example() default ""; // Description of the action

    String method() default ""; // 自定义，请求方式

    Mode mode() default Mode.All; // Mark the functionality only available to the specified mode


    public enum Mode {
        CLI,
        Web,
        All
    }
}
