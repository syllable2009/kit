package com.jxp.component.customer.service;

import com.jxp.component.customer.dto.SessionCacheDTO;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:52
 */
public interface SessionService {

    SessionCacheDTO getByUserId(String userId);

    SessionCacheDTO newSession(SessionCacheDTO dto);

    void endSession(SessionCacheDTO session);
}
