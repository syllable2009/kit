package com.jxp.component.customer.service.impl;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.MessageCallback;
import com.jxp.component.customer.dto.SessionCacheDTO;
import com.jxp.component.customer.service.ApiService;
import com.jxp.component.customer.service.ConfigService;
import com.jxp.component.customer.service.SessionService;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:50
 */
@Slf4j
@Service
public class ApiServiceImpl implements ApiService {

    @Resource
    private SessionService sessionService;
    @Resource
    private ConfigService configService;

    @Override
    public void handleMessageCallback(MessageCallback messageCallback) {
        // 消息去重

        // 事件流程控制判断
        final String eventType = messageCallback.getEventType();
        switch (eventType) {
            case "enterApp":
                // 进入app
                onEnterAppEvent(messageCallback);
                break;
            case "groupChat":
                // 群消息，需要引导到消息号
                break;
            case "customerServiceGroup":
                // 客服群,此时消息需要传给用户
                break;
            case "groupRemoveUser":
            case "groupAddUser":
                // 客服群人员变化
                break;
            case "userMessage":
                // 用户单独向消息号发送信息
                onUserMessageEvent(messageCallback);
                break;
            default:
                // 其他类型，打个日志
                break;
        }
    }

    // 处理用户向应用号发消息，保证消息顺序性，防止并发问题
    private void onUserMessageEvent(MessageCallback messageCallback) {
        // 确定会话信息：状态，startSeqId
        SessionCacheDTO session = sessionService.getByUserId(messageCallback.getUserId());
        if (null == session) {
            // 开始一个新会话
            session = sessionService.newSession(null);
        } else {
            final AppSessionConfigDTO appSessionConfig = configService.getAppSessionConfig(messageCallback.getAppId());
            // 会话是否过期，是否有效
            if (1 == 0) {
                // 开始一个新会话
                session = sessionService.newSession(null);
            }
        }
        handleUserMessageEvent(session, messageCallback);
    }

    private void handleUserMessageEvent(@NotNull SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {
        // 判断会话阶段
        if (StrUtil.equals("robot", session.getState())) {
            // 先判断session是否需要升级，升级可以成为一个会话，也可以分开
            final AppManualConfigDTO dto = configService.getManualConfigDTO(messageCallback.getAppId());
            if (dto.getKeyword().contains(messageCallback.getContent())) {
                // 会话升级，人工会话拦截，分配客服or留言，拉群
            } else {
                // 处理机器人会话
                return;
            }
        } else if (StrUtil.equals("manual", session.getState())) {
            // 找到群，发送消息
        } else {

        }

    }

    private void onEnterAppEvent(MessageCallback messageCallback) {
        // 进入事件不影响会话状态，因为事件没有messageKey，无法记录
    }
}
