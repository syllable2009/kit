package com.jxp.component.ai.handler;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:46
 */
public interface RequestHandler {
    void handle(Request request, RequestFilterChain filterChain);
}
