package com.jxp.hotline.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.message.SendMessageRequest;
import com.jxp.hotline.service.MessageService;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-21 17:30
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public String sendMessage(String appId, SendMessageRequest message) {
        final String messageKey = IdUtil.fastSimpleUUID();
        log.info("sendMessage exec,messageKey:{},appId:{},message:{}", messageKey, appId, JSONUtil.toJsonStr(message));
        return messageKey;
    }

    @Override
    public String sendNoticeMessage(String templateId, Map<String, String> paramId) {
        return null;
    }

    @Override
    public String sendCardMessage(String templateId, Map<String, String> paramId) {
        return null;
    }
}
