package com.jxp.resultcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 15:33
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface IExceptionCode {
    int code();

    String zhCN();

    String enUS();
}
