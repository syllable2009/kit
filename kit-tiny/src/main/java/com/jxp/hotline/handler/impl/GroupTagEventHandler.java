package com.jxp.hotline.handler.impl;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.domain.entity.SessionEntity.SessionEntityBuilder;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.LocalDateTimeUtil;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("groupTag")
public class GroupTagEventHandler implements EventHandler {

    @Resource
    private SessionManageService manualSessionManageService;
    @Resource
    private SessionService sessionService;

    @Override
    public void handle(MessageEvent event) {
        log.info("groupTag handler,event:{}", JSONUtil.toJsonStr(event));
        final String appId = event.getAppId();
        final String userId = event.getFrom().getUserId();
        SessionEntity activeSession = sessionService.getActiveSessionByGroupId(appId, event.getSessionId());
        // 此处可以设置客服创建会话
        if (null == activeSession) {
            // 没有会话，需要加锁创建会话
            log.info("group handler,create new session,appId:{},userId:{}", appId, userId);
            activeSession = manualSessionManageService.createSession(generateNewSession(event));
            // 没有创建成功会话直接返回
            if (null == activeSession) {
                log.error("group handler return,create new session fail,appId:{},userId:{}",
                        appId, userId);
                return;
            }
        }
        // 客服把消息推给用户
        manualSessionManageService.processManualMessageToUserEvent(activeSession, event);
    }

    @Override
    public String getName() {
        return "GroupTagEventHandler";
    }

    private SessionEntity generateNewSession(MessageEvent event) {
        final Long timestamp = event.getTimestamp();
        final LocalDateTime now = LocalDateTimeUtil.timestampToLocalDateTime(timestamp);
        final SessionEntityBuilder builder = SessionEntity.builder()
                .appId(event.getAppId())
                .userId("") // 客服建立和谁聊天
                .sid(IdUtil.fastSimpleUUID())
                .sessionFirstMessageId(event.getInfo().getMessageKey())
                .createTime(now)
                .updateTime(now)
                .noRequest(true)
                .noReply(true)
                .userRequestRobotNum(0)
                .userRequestManualNum(0)
                .manulReplyNum(0);
        final String sessionType = event.getSessionType();
        boolean ifUserSend = StrUtil.equals("p2p", sessionType);
        if (ifUserSend) {
            // 用户发起的会话，区分userToManual/userConfirm
            builder.sessionFrom("userToManual")
                    .sessionType("bot")
                    .sessionState("botChat");
        } else {
            // 客服发起的会话，直接为人工会话
            builder.sessionFrom("manualToUser")
                    .sessionType("manual")
                    .sessionState("muanualChat");

        }
        return builder.build();
    }
}
