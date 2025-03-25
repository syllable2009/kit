package com.jxp.hotline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.jxp.hotline.domain.dto.CustomerGroupDTO;
import com.jxp.hotline.domain.dto.DistributeAssitant;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

public interface SessionManageService {

    List<CustomerGroupDTO> matchLiveGroup(MessageEvent event);

    SessionEntity getLastActiveSession(String appId, String userId);

    SessionEntity createSession(SessionEntity session);

    // 记录用户的最后一条消息信息
    void recordUserLastMessage(SessionEntity session, MessageEvent event);

    void endSession(SessionEntity session);

    // 强制转接会话人
    Boolean forwardManualSession(SessionEntity session, String assistantId);

    // 尝试处理分配客服的场景，可能分配不到，如果分配到了，创建会话失败时，需要给客服补偿单应用会话数和会话总数
    void tryDistributeManualSession(SessionEntity session, AssistantGroupInfo groupInfo,
            String sessionFrom, MessageEvent event);

    // 在组里尝试分配该客服，或者指定客服，如果指定客服，则只会给该客服分配
    // 适合客服上线场景调用
    DistributeAssitant doGroupDistributeAssistant(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo);

    // 分配排队会话，分配失败会补偿
    void distributeQueueSession(SessionEntity session);

    // 处理会话结束事件
    void handleManualSessionEndEvent(SessionEntity dbSession);

    // 处理强制转接客服操作，可以突破转接客服上线
    void handleForwardSessionEvent(String sessionId, String assitantId);

    // 消费客服上线事件，客服上线会打满直至饱和，用户的上线操作也会触发给其他在线客服分配
    void handleAssitantOnlineEvent(String assitantId);

    // 用户给消息号发送消息
    void processUserMessageToAppEvent(SessionEntity session, MessageEvent event);

    // 客服给用户发送，需要记录转发以后的messageKey
    void processManualMessageToUserEvent(SessionEntity session, MessageEvent event);

    void processRobotMessageToUserEvent(SessionEntity session, String messageKey, LocalDateTime messageTime);

    // 给用户发送系统消息
    String processNoticeMessageToUserEvent(SessionEntity session, String templateId, Map<String, String> paramId);

    // 给用户发送卡片消息
    String processMixcardMessageToUserEvent(SessionEntity session, String templateId, Map<String, String> paramId);
}
