package com.jxp.component.customer.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.component.customer.dto.SessionCacheDTO;
import com.jxp.component.customer.service.SessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:38
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Override
    public SessionCacheDTO getByUserId(String userId) {
        return null;
    }

    @Override
    public SessionCacheDTO newSession(SessionCacheDTO dto) {
        return null;
    }
}
