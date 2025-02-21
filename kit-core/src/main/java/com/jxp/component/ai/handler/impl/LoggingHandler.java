package com.jxp.component.ai.handler.impl;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.RequestHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:52
 */
public class LoggingHandler implements RequestHandler {
    @Override
    public void handle(Request request, RequestFilterChain filterChain) {
        System.out.println("Logging request data: " + request.getAmount());
        filterChain.doFilter(request);
    }
}
