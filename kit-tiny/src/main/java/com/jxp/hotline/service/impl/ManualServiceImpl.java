package com.jxp.hotline.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.ManualService;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-21 15:18
 */
@Slf4j
@Service
public class ManualServiceImpl implements ManualService {

    @Override
    public void processManualMessage(SessionEntity sessionEntity, MessageEvent event) {
        // 转发消息后生成的messageKey
        String messageKey = "";
        sessionEntity.setManulLastMessageId(messageKey);
        final Long timestamp = event.getTimestamp();
        final LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        sessionEntity.setManulLastMessageTime(localDateTime);
        sessionEntity.setManulReplyNum(sessionEntity.getUserRequestManualNum() + 1);
        // 需要处理第一条客服发送时间和messageKey
        if (BooleanUtil.isTrue(sessionEntity.getNoReply())) {

        }
    }

    @Override
    public void processUserMessage(SessionEntity session, MessageEvent event) {
    }
}
