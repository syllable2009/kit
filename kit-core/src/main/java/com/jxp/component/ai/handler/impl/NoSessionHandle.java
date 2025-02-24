package com.jxp.component.ai.handler.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxp.component.ai.dto.Request;
import com.jxp.component.ai.dto.RequestFilterChain;
import com.jxp.component.ai.handler.RequestHandler;
import com.jxp.component.customer.service.AiService;
import com.jxp.component.customer.service.ConfigService;
import com.jxp.component.customer.service.ManualService;
import com.jxp.component.customer.service.MessageService;
import com.jxp.component.customer.service.SessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 17:30
 */
@Slf4j
@Service
public class NoSessionHandle implements RequestHandler {

    @Resource
    private SessionService sessionService;
    @Resource
    private ConfigService configService;
    @Resource
    private AiService aiService;
    @Resource
    private MessageService messageService;
    @Resource
    private ManualService manualService;

    @Override
    public void handle(Request request, RequestFilterChain filterChain) {

    }
}
