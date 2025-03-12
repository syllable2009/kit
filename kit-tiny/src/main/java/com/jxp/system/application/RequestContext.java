package com.jxp.system.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求上下文
 * @author jiaxiaopeng
 * Created on 2025-03-12 14:50
 */

@NoArgsConstructor
@Data
@Slf4j
public class RequestContext {

    private List<String> requsetCommand = new ArrayList<>();

    private Map<String, Object> requestOptions = new ConcurrentHashMap<>();

    public void parserParams(String[] args) {

    }
}
