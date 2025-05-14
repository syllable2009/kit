package com.jxp.delayevent;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-14 15:50
 */
@Slf4j
public class ManualSessionTimeoutHandle implements DelayEventHandle {

    @Resource
    private DelayEventService delayEventService;

    @Override
    public DelayEventType getType() {
        return DelayEventType.MANUAL_SESSION_TIMEOUT;
    }

    @Override
    public void triggerDelayEvent(DelayEvent event) {
        log.info("ManualSessionTimeoutHandle start,event:{}", event);
        final String sessionId = event.getUniqueId();
        // 查询会话

        //
    }
}
