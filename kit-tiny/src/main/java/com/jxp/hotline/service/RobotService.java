package com.jxp.hotline.service;

import com.jxp.hotline.domain.dto.MessageEvent;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:57
 */
public interface RobotService {

    void processUserMessage(MessageEvent event);
}
