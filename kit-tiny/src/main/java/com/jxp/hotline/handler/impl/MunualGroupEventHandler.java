package com.jxp.hotline.handler.impl;

import javax.annotation.Resource;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.SessionService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("munualGroup")
public class MunualGroupEventHandler implements EventHandler {

    @Resource
    private SessionService sessionService;

    @Override
    public void handle(MessageEvent event) {
        log.info("munualGroup handler,event:{}", JSONUtil.toJsonStr(event));
        final String appId = event.getAppId();
    }

    @Override
    public String getName() {
        return "MunualGroupEventHandler";
    }
}
