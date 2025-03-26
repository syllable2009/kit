package com.jxp.hotline.handler.impl;

import javax.annotation.Resource;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.MessageService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("group")
public class GroupEventHandler  implements EventHandler {

    @Resource
    private MessageService messageService;

    @Override
    public void handle(MessageEvent event) {
        log.info("group handler,event:{}", JSONUtil.toJsonStr(event));
        // 发送引导到消息号的信息，没有会话操作
        messageService.sendCardMessage("clickToApp", null);
    }

    @Override
    public String getName() {
        return "GroupEventHandler";
    }
}
