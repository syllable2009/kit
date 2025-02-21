package com.jxp.component.ai.dto;

import java.util.ArrayList;
import java.util.List;

import com.jxp.component.ai.handler.RequestHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:47
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestFilterChain {
    private List<RequestHandler> handlers = new ArrayList<>();
    private int index = 0;

    public void addHandler(RequestHandler handler) {
        handlers.add(handler);
    }

    public void doFilter(Request request) {
        if (index < handlers.size()) {
            RequestHandler currentHandler = handlers.get(index);
            index++;
            currentHandler.handle(request, this);
        }
    }
}
