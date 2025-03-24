package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.entity.SessionEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:38
 */

@Service
@Slf4j
public class ManualSessionManageService extends DefaultSessionManageService {

    @Override
    public void doAfterSessionCreate(SessionEntity session) {
        super.doAfterSessionCreate(session);
    }

    @Override
    public void doAfterSessionEnd(SessionEntity session) {
    }
}
