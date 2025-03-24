package com.jxp.hotline.service;

import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

public interface SessionManageService {

    void createSession(SessionEntity session);

    void endSession(SessionEntity session);
}
