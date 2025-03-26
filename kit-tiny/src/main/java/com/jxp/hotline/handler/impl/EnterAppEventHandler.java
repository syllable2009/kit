package com.jxp.hotline.handler.impl;

import java.time.Duration;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.constant.SessionLockKey;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.MessageService;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.JedisCommands;
import com.jxp.hotline.utils.JedisUtils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("enterApp")
public class EnterAppEventHandler implements EventHandler {

    // 默认的缓存时间
    private static final Duration WELCOME_CACHE_TIMEOUT = Duration.ofHours(1);
    @Autowired(required = false)
    private JedisCommands jedisCommands;
    @Resource
    private SessionManageService robotSessionManageService;
    @Resource
    private MessageService messageService;
    @Resource
    private SessionService sessionService;

    @Override
    public void handle(MessageEvent event) {
        log.info("enterApp handler,event:{}", JSONUtil.toJsonStr(event));
        final String appId = event.getAppId();
        final String userId = event.getFrom().getUserId();
        final String redisKey = SessionLockKey.format(SessionLockKey.welcomeSendKey, appId, userId);
        // 超过一定时间不发
        final String lastSendTime = JedisUtils.get(jedisCommands, redisKey);
        if (StrUtil.isNotBlank(lastSendTime)) {
            // 还未过期
            return;
        }
        // 有会话不发
        final SessionEntity activeSession = sessionService.getActiveSessionByUserId(appId, userId);
        if (null != activeSession) {
            return;
        }
        // 其他的不发条件

        // 发送并记录时间
        // 根据配置，生成card发送
        messageService.sendCardMessage("welcomeCard", null);
        JedisUtils.setex(jedisCommands, redisKey, WELCOME_CACHE_TIMEOUT.getSeconds(), Long.toString(System.currentTimeMillis()));
    }

    @Override
    public String getName() {
        return "EnterAppEventHandler";
    }
}
