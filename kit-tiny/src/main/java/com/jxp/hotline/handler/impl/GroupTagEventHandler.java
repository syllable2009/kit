package com.jxp.hotline.handler.impl;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.handler.EventHandler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("groupTag")
public class GroupTagEventHandler implements EventHandler {
    @Override
    public void handle(MessageEvent event) {
        log.info("groupTag handler,event:{}", JSONUtil.toJsonStr(event));
    }

    @Override
    public String getName() {
        return "GroupTagEventHandler";
    }
}
