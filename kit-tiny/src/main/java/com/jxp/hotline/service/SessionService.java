package com.jxp.hotline.service;

import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:33
 */
public interface SessionService {
    SessionEntity getActiveSession(String messageServerId, String userId);

    SessionEntity getSessionBySid(String sessionId);

    Boolean createSession(SessionEntity sessionEntity);
}
