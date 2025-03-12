package com.jxp.system.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.jxp.system.domain.Mode;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-12 15:22
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    String value(); // The URI path or command name

    String description() default ""; // Description of the action

    Argument[] arguments() default {}; // Arguments expected by the action

    Argument[] options() default {}; // Command-line options

    String example() default ""; // Description of the action

    String method() default ""; // 自定义，请求方式

    Mode mode() default Mode.All; // Mark the functionality
    // only available to the specified mode
}
