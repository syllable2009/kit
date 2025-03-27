package com.jxp.hotline.mq;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxp.hotline.service.DelayEventQueueService;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-27 14:03
 */
@Service
public class PulsarConsumer {


    @Resource
    private DelayEventQueueService delayEventQueueService;

    //    @PulsarListener(
//            topics = "persistent://public/default/my-topic",
//            subscriptionName = "my-subscription",
//            subscriptionType = SubscriptionType.Shared
//    )
    public void consumerSessionTimeoutMessage(String message) {
        // 处理消息逻辑
        delayEventQueueService.handleSessionTimeoutMessage(null);
    }

    public void consumerSessionManualTimeoutMessage(String message) {
        // 处理消息逻辑
        delayEventQueueService.handleSessionManualTimeoutMessage(null);
    }

    public void consumerSessionUserTimeoutMessage(String message) {
        // 处理消息逻辑
        delayEventQueueService.handleSessionUserTimeoutMessage(null);
    }

    public void consumerTransferQueueTimeoutMessage(String message) {
        // 处理消息逻辑
        delayEventQueueService.handleTransferQueueTimeoutMessage(null);
    }

    public void consumerLeaveMessageTimeoutMessage(String message) {
        // 处理消息逻辑
        delayEventQueueService.handleLeaveMessageTimeoutMessage(null);
    }
}
