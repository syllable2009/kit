package com.jxp.hotline.service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:57
 */
public interface ManualService {

    // 处理人工会话客服发送的消息
    void processManualMessage(SessionEntity session, MessageEvent event);

    // 处理人工会话用户发送的信息
    void processUserMessage(SessionEntity session, MessageEvent event);
}
