package com.jxp.tinystruct.domain;

import java.lang.invoke.MethodHandle;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-06 10:45
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Command {
    private Object instance;
    private MethodHandle methodHandle;
    private Class<?>[] parameterTypes;
    private Class<?> returnType;
    private List<CommandParam<String, Object>> params;
}
