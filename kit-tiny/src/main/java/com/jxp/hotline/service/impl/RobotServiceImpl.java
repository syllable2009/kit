package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.service.RobotService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:57
 */

@Slf4j
@Service
public class RobotServiceImpl implements RobotService {
    @Override
    public void processUserMessage(MessageEvent event) {
    }
}
