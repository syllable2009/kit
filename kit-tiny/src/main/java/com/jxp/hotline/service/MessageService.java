package com.jxp.hotline.service;

import java.util.Map;

import com.jxp.hotline.domain.message.SendMessageRequest;

/**
 * 负责message和mixcard转发
 * @author jiaxiaopeng
 * Created on 2025-03-26 11:16
 */
public interface MessageService {

    String sendMessage(String appId, SendMessageRequest message);

    // 给用户发送notice
    String sendNoticeMessage(String templateId,
            Map<String, String> paramId);

    // 给用户发送card，card是交互式可操作的信息，需要有事件回调
    String sendCardMessage(String templateId,
            Map<String, String> paramId);
}
