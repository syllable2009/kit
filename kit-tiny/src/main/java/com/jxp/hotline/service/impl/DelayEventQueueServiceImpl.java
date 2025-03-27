package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.service.DelayEventQueueService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-27 12:01
 */

@Slf4j
@Service
public class DelayEventQueueServiceImpl implements DelayEventQueueService {
    @Override
    public Boolean handleSessionTimeoutMessage(Object obj) {
        return null;
    }

    @Override
    public Boolean handleSessionManualTimeoutMessage(Object obj) {
        return null;
    }

    @Override
    public Boolean handleSessionUserTimeoutMessage(Object obj) {
        return null;
    }

    @Override
    public Boolean handleTransferQueueTimeoutMessage(Object obj) {
        return null;
    }

    @Override
    public Boolean handleLeaveMessageTimeoutMessage(Object obj) {
        return null;
    }
}
