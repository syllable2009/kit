package com.jxp.system.domain;

import java.lang.invoke.MethodHandle;
import java.util.regex.Pattern;

import com.jxp.system.annotation.Argument;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-12 15:28
 */
@Data
public class Command {

    public static final int MAX_ARGUMENTS = 10;
    private int id;
    private Pattern pattern;
    private Object app;
    private String method;
    private MethodHandle methodHandle;
    private Class<?>[] parameterTypes;
    private Class<?> returnType;
    private int priority;
    private Mode mode;
    private String pathRule;
    private Argument[] options = new Argument[]{};
}
