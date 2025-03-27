package com.jxp.hotline.service;

/**
 * 延时事件队列服务，底层用docker部署单机版的pulsar
 * @author jiaxiaopeng
 * Created on 2025-03-27 12:01
 */
public interface DelayEventQueueService {

    // 消费消息
    Boolean handleSessionTimeoutMessage(Object obj);

    Boolean handleSessionManualTimeoutMessage(Object obj);

    Boolean handleSessionUserTimeoutMessage(Object obj);

    Boolean handleTransferQueueTimeoutMessage(Object obj);

    Boolean handleLeaveMessageTimeoutMessage(Object obj);

}
