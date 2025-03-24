package com.jxp.hotline.handler.impl;

import java.util.List;

import javax.annotation.Resource;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.config.SpringUtils;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.SessionManageService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("message")
public class MessageEventHandler implements EventHandler {

    @Resource
    private SessionManageService manualSessionManageService;
    @Resource
    private SessionManageService robotSessionManageService;

    @Override
    public void handle(MessageEvent event) {
        log.info("message handler,event:{}", JSONUtil.toJsonStr(event));
        final String sessionType = event.getSessionType();
        // 客服群的消息直接转发
        if (StrUtil.equals("groupTag", sessionType)) {
            final EventHandler groupTagHandler = SpringUtils.getBean("groupTagEventHandler");
            if (null != groupTagHandler) {
                groupTagHandler.handle(event);
                return;
            }
            log.info("message handler return,groupTagHandler is null,event:{}", JSONUtil.toJsonStr(event));
            return;
        } else if (StrUtil.equals("group", sessionType)) {
            // 普通群的消息直接转发
            final EventHandler groupHandler = SpringUtils.getBean("groupEventHandler");
            if (null != groupHandler) {
                groupHandler.handle(event);
                return;
            }
            log.info("message handler return,groupHandler is null,event:{}", JSONUtil.toJsonStr(event));
            return;
        }

        // 用户对app的消息
        final String messageServerId = event.getAppId();
        final String userId = event.getFrom().getUserId();
        // 获取该应用下唯一的一个在线会话，可以缓存在redis
        SessionEntity activeSession = manualSessionManageService.getLastActiveSession(messageServerId, userId);
        if (null == activeSession) {
            // 没有会话，需要加锁创建会话
            log.info("message handler,create new robot session,messageServerId:{},userId:{}", messageServerId,
                    userId);
            activeSession = robotSessionManageService.createNewSession(event);
            // 没有创建成功会话直接返回
            if (null == activeSession) {
                log.error("message handler return,no found active session,messageServerId:{},userId:{}",
                        messageServerId,
                        userId);
                return;
            }
        } else {
            // 记录会话的last相关信息
            robotSessionManageService.recordUserLastMessage(activeSession, event);
        }

        // 如果处于人工会话中则直接发给人工
        if (StrUtil.equals("manual", activeSession.getSessionType())) {
            // 人工会话处理
            log.info("message handler,sendUserMessageToLiveCustomer,messageServerId:{},userId:{}", messageServerId,
                    userId);
            // 发给客服
            manualSessionManageService.processManualMessageToUserEvent(activeSession, event);
            return;
        }
        // 否则进行转人工规则匹配，匹配到组，如果这里需要详细，也可以封装返回一个详细DTO对象
        final List<AssistantGroupInfo> assistantGroups = robotSessionManageService.matchLiveGroup(event);
        if (CollUtil.isEmpty(assistantGroups)) {
            //进入机器人会话
            robotSessionManageService.processUserMessageToAppEvent(activeSession, event);
        } else if (1 == assistantGroups.size()) {
            // 尝试开始分配客服转人工
            robotSessionManageService.tryDistributeManualSession(activeSession, assistantGroups.get(0), "userToManual");
        } else {
            // 发送选择技能队列卡片，此时还是机器人会话，选择卡片以后调用distributeManualSession方法
            sendUserChooseGroupMessage(activeSession, event, assistantGroups);
        }
    }

    @Override
    public String getName() {
        return "MessageEventHandler";
    }

    private void sendUserChooseGroupMessage(SessionEntity session, MessageEvent event, List<AssistantGroupInfo> assistantGroups) {
        // 构造参数
        robotSessionManageService.processMixcardMessageToUserEvent(session, "userChooseGroupMessage", null);
    }
}
