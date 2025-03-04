package com.jxp.component.ai.strategy.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-04 11:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface HandlerType {
    String value(); // 如 "TYPE_A", "TYPE_B"
}
