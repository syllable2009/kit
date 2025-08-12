package com.jxp.test;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-08-12 15:33
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuilderClass {
    private String clazz;
    private Map<String, String> config;
    private Map<String, String> fieldMap = new HashMap<>();
}
