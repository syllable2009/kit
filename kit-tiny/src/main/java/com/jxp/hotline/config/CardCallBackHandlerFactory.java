package com.jxp.hotline.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jxp.hotline.handler.AbstractCardCallBackHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 15:55
 */
@Component
public class CardCallBackHandlerFactory {
    private final Map<String, AbstractCardCallBackHandler> parserMap = new HashMap<>();

    @Autowired
    public void setParserMap(Set<AbstractCardCallBackHandler> handlers) {
        handlers.forEach(handler -> parserMap.put(handler.getBizId(), handler));
    }

    public AbstractCardCallBackHandler getHandler(String bizId) {
        return parserMap.get(bizId);
    }
}
