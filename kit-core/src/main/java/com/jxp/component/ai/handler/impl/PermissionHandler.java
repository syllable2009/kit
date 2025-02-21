package com.jxp.component.ai.handler.impl;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.RequestHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:52
 */
public class PermissionHandler implements RequestHandler {
    @Override
    public void handle(Request request, RequestFilterChain filterChain) {
        if (checkPermissions(request.getUsername())) {
            System.out.println("Permission pass for user: " + request.getUsername());
            filterChain.doFilter(request);
        } else {
            System.out.println("Permission denied for user: " + request.getUsername());
        }
    }

    private boolean checkPermissions(String username) {
        // 实现权限检查逻辑
        return username.equals("admin");
    }
}
