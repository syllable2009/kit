package com.jxp.hotline.service.impl;

import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.SessionManageService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

@Slf4j
public abstract class DefaultSessionManageService implements SessionManageService {

    @Override
    public void createSession(SessionEntity session) {
        doAfterSessionCreate(session);
    }

    public void doAfterSessionCreate(SessionEntity session) {
    }

    @Override
    public void endSession(SessionEntity session) {
        // 结束掉会话
        doAfterSessionEnd(session);
    }

    public void doAfterSessionEnd(SessionEntity session) {
    }
}
