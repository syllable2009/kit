package com.jxp.hotline.handler;

import com.jxp.hotline.domain.dto.MessageEvent;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:03
 */
public interface EventHandler {

    void handle(MessageEvent event);

    default String getName() {
        return null;
    }
}
