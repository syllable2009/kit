package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.SessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:33
 */

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {
    @Override
    public SessionEntity getActiveSession(String messageServerId, String userId) {
        return null;
    }

    @Override
    public SessionEntity getSessionBySid(String sessionId) {
        return null;
    }

    @Override
    public Boolean createSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public Boolean upgradeQueueSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public Boolean upgradeManualSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public Boolean distributeSession(SessionEntity sessionEntity) {
        return null;
    }

}
