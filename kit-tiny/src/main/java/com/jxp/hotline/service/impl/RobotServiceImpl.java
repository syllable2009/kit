package com.jxp.hotline.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.RobotService;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:57
 */

@Slf4j
@Service
public class RobotServiceImpl implements RobotService {

    @Override
    public void processUserMessage(SessionEntity sessionEntity, MessageEvent event) {

        // 转发消息后生成的messageKey
        String messageKey = "";
        sessionEntity.setUserLastMessageId(messageKey);
        final Long timestamp = event.getTimestamp();
        final LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        sessionEntity.setUserLastMessageTime(localDateTime);
        if (StrUtil.equals("manual", sessionEntity.getSessionType())) {
            sessionEntity.setUserRequestManualNum(sessionEntity.getUserRequestManualNum() + 1);
        } else {
            sessionEntity.setUserRequestRobotNum(sessionEntity.getUserRequestManualNum() + 1);
        }

        // 需要处理第一条用户发送时间和messageKey
        if (BooleanUtil.isTrue(sessionEntity.getNoRequest())) {
        }
    }

    @Override
    public void sendUserChooseGroupMessage(SessionEntity session, MessageEvent event, List<AssistantGroupInfo> assistantGroups) {

    }
}
