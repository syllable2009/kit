package com.jxp.hotline.service.impl;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.ManualService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.LocalDateTimeUtil;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *  处理人工session会话下的操作
 * @author jiaxiaopeng
 * Created on 2025-03-21 15:18
 */
@Slf4j
@Service
public class ManualServiceImpl implements ManualService {

    @Resource
    private SessionService sessionService;

    @Override
    public void processManualMessage(SessionEntity session, MessageEvent event) {
        // 转发消息后生成的messageKey
        String messageKey = "";
        final SessionEntity entity = SessionEntity.builder()
                .sid(session.getSid())
                .build();
        final LocalDateTime messageTime = LocalDateTimeUtil.timestampToLocalDateTime(event.getTimestamp());
        entity.setManulLastMessageTime(messageTime);
        entity.setManulLastMessageId(messageKey);
        // 需要原子性+1操作
        entity.setManulReplyNum(session.getUserRequestManualNum() + 1);
        // 需要处理第一条客服发送时间和messageKey
        if (BooleanUtil.isTrue(session.getNoReply())) {
            entity.setNoReply(false);
            entity.setManulFirstMessageId(messageKey);
            entity.setManulFirstMessageTime(messageTime);
        }
    }

    @Override
    public void processUserMessage(SessionEntity session, MessageEvent event) {
        // 转发消息后生成的messageKey
        String messageKey = "";
        final SessionEntity entity = SessionEntity.builder()
                .sid(session.getSid())
                .build();
        final LocalDateTime messageTime = LocalDateTimeUtil.timestampToLocalDateTime(event.getTimestamp());
        if (BooleanUtil.isTrue(session.getNoRequest())) {
            // 第一次提问
            entity.setNoRequest(false);
            entity.setUserFistMessageId(messageKey);
            entity.setUserFistMessageTime(messageTime);
        }
        entity.setUserLastMessageId(messageKey);
        entity.setUserLastMessageTime(messageTime);
        // 需要原子性+1操作
//        entity.setUserRequestManualNum();

    }
}
