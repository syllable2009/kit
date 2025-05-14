package com.jxp.delayevent;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-14 15:46
 */
public interface DelayEventHandle {

    /**
     * 负责处理的事件类型
     */
    DelayEventType getType();

    /**
     * 触发了延迟事件
     */
    void triggerDelayEvent(DelayEvent event);
}
