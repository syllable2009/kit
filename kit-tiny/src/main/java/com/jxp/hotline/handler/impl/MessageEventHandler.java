package com.jxp.hotline.handler.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.config.SpringUtils;
import com.jxp.hotline.domain.dto.CustomerGroupDTO;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.domain.entity.SessionEntity.SessionEntityBuilder;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.MessageService;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.LocalDateTimeUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
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
    @Resource
    private MessageService messageService;
    @Resource
    private SessionService sessionService;

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
        final String appId = event.getAppId();
        final String userId = event.getFrom().getUserId();
        // 获取该应用下唯一的一个在线会话，可以缓存在redis
        SessionEntity activeSession = sessionService.getActiveSessionByUserId(appId, userId);
        if (null == activeSession) {
            // 没有会话，需要加锁创建会话
            log.info("message handler,create new session,appId:{},userId:{}", appId,
                    userId);
            activeSession = robotSessionManageService.createSession(generateNewSession(event));
            // 没有创建成功会话直接返回
            if (null == activeSession) {
                log.error("message handler return,create new session fail,appId:{},userId:{}",
                        appId, userId);
                return;
            }
        }

        // 如果处于人工会话中则直接发给人工
        if (StrUtil.equals("manual", activeSession.getSessionType())) {
            // 人工会话处理
            log.info("message handler,sendUserMessageToLiveCustomer,appId:{},userId:{}", appId,
                    userId);
            // 用户消息发给客服
            manualSessionManageService.processUserMessageToManualEvent(activeSession, event);
            return;
        }
        // 否则进行转人工规则匹配，匹配到组，如果这里需要详细，也可以封装返回一个详细DTO对象
        final List<CustomerGroupDTO> customerGroupDTOS = robotSessionManageService.matchLiveGroup(event);
        if (CollUtil.isEmpty(customerGroupDTOS)) {
            //进入机器人会话
            robotSessionManageService.processUserMessageToRobotEvent(activeSession, event);
        } else if (1 == customerGroupDTOS.size()) {
            final CustomerGroupDTO customerGroupDTO = customerGroupDTOS.get(0);
            // 获取组的配置
            AssistantGroupInfo assistantGroupInfo = null;
            // 尝试开始分配客服转人工
            robotSessionManageService.tryDistributeManualSession(activeSession, assistantGroupInfo, "userToManual", event);
        } else {
            // 发送卡片展示的组名称可以是自定义的，选中单个组才会实时查询组的服务状态信息等
            // 发送选择技能队列卡片，此时还是机器人会话，选择卡片以后调用distributeManualSession方法
            final String messageKey = sendUserChooseGroupMessage(activeSession, event, customerGroupDTOS);
            if (StrUtil.isBlank(messageKey)) {
                log.error("message handler,sendUserChooseGroupMessage error");
                return;
            }
            robotSessionManageService.processRobotMessageToUserEvent(activeSession, messageKey, LocalDateTimeUtil.now());
        }
    }

    @Override
    public String getName() {
        return "MessageEventHandler";
    }

    private String sendUserChooseGroupMessage(SessionEntity session, MessageEvent event,
            List<CustomerGroupDTO> assistantGroups) {
        // 构造参数发送
        return messageService.sendCardMessage("userChooseGroupMessage", null);
    }

    private SessionEntity generateNewSession(MessageEvent event) {
        final Long timestamp = event.getTimestamp();
        final LocalDateTime now = LocalDateTimeUtil.timestampToLocalDateTime(timestamp);
        final SessionEntityBuilder builder = SessionEntity.builder()
                .appId(event.getAppId())
                .userId(event.getFrom().getUserId())
                .sid(IdUtil.fastSimpleUUID())
                .sessionFirstMessageId(event.getInfo().getMessageKey())
                .createTime(now)
                .updateTime(now);
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
