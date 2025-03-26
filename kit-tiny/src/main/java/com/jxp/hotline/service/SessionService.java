package com.jxp.hotline.service;

import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * session的crud
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:33
 */
public interface SessionService {

    SessionEntity getActiveSessionByUserId(String appId, String userId);

    SessionEntity getActiveSessionByGroupId(String appId, String groupId);

    SessionEntity getSessionBySid(String sessionId);

    Boolean createSession(SessionEntity sessionEntity);

    // 升级为排队会话
    Boolean upgradeQueueSession(SessionEntity sessionEntity);

    // 升级为人工会话
    Boolean upgradeManualSession(SessionEntity sessionEntity);

    // 分配会话，仅更新相关字段
    Boolean distributeSession(SessionEntity sessionEntity);

    // 会话开始，可以当做事件，也可以串行调用
    void handleManualSessionStartEvent(SessionEntity sessionEntity);

    // 会话结束，可以当做事件，也可以串行调用
    void handleManualSessionEndEvent(SessionEntity sessionEntity);

    Boolean endSession(SessionEntity session);

    // 客服发送消息后更新会话信息
    Boolean manualUpdateSession(SessionEntity session);

    // 用户发送消息后更新会话信息
    Boolean userUpdateSession(SessionEntity session);
}
