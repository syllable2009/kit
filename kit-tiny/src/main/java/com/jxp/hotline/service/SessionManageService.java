package com.jxp.hotline.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jxp.hotline.domain.dto.CustomerGroupDTO;
import com.jxp.hotline.domain.dto.DistributeAssitant;
import com.jxp.hotline.domain.dto.ForwardSessionDTO;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

public interface SessionManageService {

    // 匹配转人工规则的客服组
    List<CustomerGroupDTO> matchLiveGroup(MessageEvent event);

    // 加锁创建会话
    SessionEntity createSession(SessionEntity session);

    // 处理用户发给人工客服的消息，只能是人工会话
    void processUserMessageToManualEvent(SessionEntity session, MessageEvent event);

    // 处理用户发给机器人的消息，只能是机器人会话
    void processUserMessageToRobotEvent(SessionEntity session, MessageEvent event);

    // 客服给用户发送消息，需要记录转发以后的messageKey
    void processManualMessageToUserEvent(SessionEntity session, MessageEvent event);

    // 机器人向用户发送消息：安抚，提醒等系统消息，只会更新endMessageKey
    void processRobotMessageToUserEvent(SessionEntity session, String messageKey, LocalDateTime messageTime);

    // 机器人向客服发送消息：提醒等系统消息，只会更新endMessageKey
    void processRobotMessageToManualEvent(SessionEntity session, String messageKey, LocalDateTime messageTime);

    // 操作：结束会话，结束原因，操作人
    Boolean endSession(SessionEntity session);

    // 操作：处理强制转接客服操作，可以突破转接客服上线
    Boolean handleForwardSessionEvent(ForwardSessionDTO forward);

    // 尝试处理分配客服的场景，可能分配不到，如果分配到了，创建会话失败时，需要给客服补偿单应用会话数和会话总数
    void tryDistributeManualSession(SessionEntity session, AssistantGroupInfo groupInfo,
            String sessionFrom, MessageEvent event);

    // 在组里尝试分配该客服，或者指定客服，如果指定客服，则只会给该客服分配
    DistributeAssitant doGroupDistributeAssistant(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo);

    // 分配排队会话，分配失败会补偿
    Boolean distributeQueueSession(SessionEntity session);

    // 事件：消费客服上线事件，客服上线会打满直至饱和，用户的上线操作也会触发给其他在线客服分配
    void handleAssitantOnlineEvent(String assitantId);

    // 操作：领取排队会话，可以突破转接客服上线
    Boolean claimQueueSession(String sessionId);

    void userUpdateSession(SessionEntity session, MessageEvent event);
}
