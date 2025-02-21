package com.jxp.component.ai.handler.impl;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.RequestHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:51
 */
public class AuthenticationHandler implements RequestHandler {
    @Override
    public void handle(Request request, RequestFilterChain filterChain) {
        if (authenticate(request.getUsername())) {
            System.out.println("Authentication success for user: " + request.getUsername());
            filterChain.doFilter(request);
        } else {
            System.out.println("Authentication failed for user: " + request.getUsername());
        }
    }

    private boolean authenticate(String username) {
        // 实现身份验证逻辑
        return username.equals("admin");
    }
}




