package com.jxp.hotline.service;

import com.jxp.hotline.domain.message.SendMessageRequest;

/**
 * 负责message和mixcard转发
 * @author jiaxiaopeng
 * Created on 2025-03-26 11:16
 */
public interface MessageService {
    String sendMessage(String appId, SendMessageRequest message);
}
