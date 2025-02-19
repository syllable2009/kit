package com.jxp.component.customer.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.AppWelcomeConfigDTO;
import com.jxp.component.customer.dto.ManualGroupConfigDTO;
import com.jxp.component.customer.dto.MessageCallback;
import com.jxp.component.customer.dto.SessionCacheDTO;
import com.jxp.component.customer.service.AiService;
import com.jxp.component.customer.service.ApiService;
import com.jxp.component.customer.service.ConfigService;
import com.jxp.component.customer.service.ManualService;
import com.jxp.component.customer.service.MessageService;
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
    @Resource
    private AiService aiService;
    @Resource
    private MessageService messageService;
    @Resource
    private ManualService manualService;

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
        if (StrUtil.equals("robot", session.getType())) {
            // 先判断session是否需要升级，升级可以成为一个会话，也可以分开
            final AppManualConfigDTO dto = configService.getManualConfigDTO(messageCallback.getAppId());
            final boolean tryResult = judgeUpgradeSession(dto, session, messageCallback);
            if (!tryResult) {
                // 处理机器人会话
                aiService.llmgc(messageCallback.getContent());
            }
        } else if (StrUtil.equals("manual", session.getType())) {
            // 找到群，发送消息
        } else {
            log.error("can not handle sesseion type");
        }

    }

    // 判断是否需要升级会话，如果false走机器人，true自己处理
    private boolean judgeUpgradeSession(AppManualConfigDTO dto, @NotNull SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {
        if (0 == dto.getBlockState()) {
            // 全局拦截
            final List<String> keyword = dto.getKeyword();
            if (keyword.contains(messageCallback.getContent())) {
                // 会话升级，人工会话拦截，分配客服or留言，拉群
                if (0 == session.getState()) {
                    // 已经拦截过了，开启转人工
                    if (dto.isIfManualBlock() && 1 == session.getBlockState()) {
                        // 拦截一次
                        handleManualBlock();
                    } else {
                        // 开启会话升级
                        tryUpgradeSession(dto, session, messageCallback);
                    }
                }
                log.info("并发转人工，wait");
                return true;
            }
        } else {
            // 开启了技能队列
            final ManualGroupConfigDTO manualGroupConfig =
                    configService.getManualGroupConfig(messageCallback.getAppId());
            final Map<String, Map<String, String>> manualGroup = manualGroupConfig.getManualGroup();
            // 找到匹配关键字的组，如果未找到留言，否则走拦截
            if ("匹配到" == "") {
                // 工作时间等其他校验
                // 要么拦截，要么留言
                return true;
            }
        }
        return false;
    }

    // 尝试升级
    private void tryUpgradeSession(AppManualConfigDTO dto, @NotNull SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {
        // 是否开启技能队列

        // 会话诊断，判断是否进入留言

        // 结束当前机器人会话

        // 开始人工会话
    }

    private void onEnterAppEvent(MessageCallback messageCallback) {
        // 进入事件不影响会话状态，因为事件没有messageKey，无法记录
        final AppWelcomeConfigDTO appWelcomeConfigDTO =
                configService.getAppWelcomeConfigDTO(messageCallback.getAppId());
    }

    private void handleManualBlock() {

    }
}
