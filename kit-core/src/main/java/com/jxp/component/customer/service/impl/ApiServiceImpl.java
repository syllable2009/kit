package com.jxp.component.customer.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.AppWelcomeConfigDTO;
import com.jxp.component.customer.dto.MessageCallback;
import com.jxp.component.customer.dto.SessionCacheDTO;
import com.jxp.component.customer.dto.TransferManualItemRule;
import com.jxp.component.customer.service.AiService;
import com.jxp.component.customer.service.ApiService;
import com.jxp.component.customer.service.ConfigService;
import com.jxp.component.customer.service.ManualService;
import com.jxp.component.customer.service.MessageService;
import com.jxp.component.customer.service.SessionService;

import cn.hutool.core.collection.CollUtil;
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
            // 会话检测
            final AppSessionConfigDTO appSessionConfig = configService.getAppSessionConfig(messageCallback.getAppId());
            // 会话是否过期，是否有效
            if (1 == 0) {
                // 开始一个新会话
                session = sessionService.newSession(null);
            }
        }
        // 处理用户发送的消息(区分消息事件event)
        handleUserMessageEvent(session, messageCallback);
    }

    // 处理用户发送的消息：会话session可能为空
    private void handleUserMessageEvent(SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {
        if (null == session) {
            // 如果没有会话

        } else if (StrUtil.equals("robot", session.getType())) {
            // 判断会话阶段
            // 先判断session是否需要升级，升级可以成为一个会话，也可以分开
            final AppManualConfigDTO dto = configService.getManualConfigDTO(messageCallback.getAppId());
            final int manualType = dto.getManualType();
            if (0 == manualType) {
                // 规则引擎匹配：全量匹配，找到一个停止or所有规则匹配
                if (!matchTransferManualRule(messageCallback)) {
                    // 处理机器人会话
                    aiService.llmgc(messageCallback.getAppId(), messageCallback.getContent());
                    return;
                }
            } else if (1 == manualType) {
                // 机器人
                // 处理机器人会话
                aiService.llmgc(messageCallback.getAppId(), messageCallback.getContent());
            } else if (2 == manualType) {
                doUpgradeSession(dto, session, messageCallback);
                // 人工
                manualService.manualAnswer(messageCallback.getAppId(), messageCallback.getContent());
            } else {
                return;
            }
        } else if (StrUtil.equals("manual", session.getType())) {
            // 找到群，发送消息
        } else {
            log.error("can not handle sesseion type");
        }

    }

    private boolean matchRuleDetail(TransferManualItemRule r, MessageCallback messageCallback, Map<String, String> processMap) {
        return true;
    }

    // 匹配转人工规则，如果匹配到进行人工处理
    private boolean matchTransferManualRule(MessageCallback messageCallback) {
        // 找到匹配的组，按照weight缓存好
        final List<TransferManualItemRule> itemRuleList = configService.getTransferManualItemRule(messageCallback.getAppId());
        // 数据缓存map，复用参数
        final Map<String, String> processMap = new HashMap<>();

        // 找到优先级最高的一个，还是找完所有的规则组
        final List<TransferManualItemRule> matchItemRuleList = itemRuleList.stream()
                .filter(e -> matchRuleDetail(e, messageCallback, processMap))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(matchItemRuleList)) {
            // 没有找到人工规则，返回
            return false;
        }
        // 客服组校验
        List<String> groupIds = Lists.newArrayList();
        // 组进行人员校验

        if (groupIds.size() == 1) {
            // 一个组的话，直接进行会话升级
        } else {
            // 发送让用户选择的内容
        }
        return true;
    }

    private void doUpgradeSession(AppManualConfigDTO dto, @NotNull SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {

    }

    // 尝试升级，有拦截判断 + 人工验证 + 留言
    private void tryUpgradeSession(AppManualConfigDTO dto, @NotNull SessionCacheDTO session,
            @NotNull MessageCallback messageCallback) {
        if (null == session) {

        }
        if (0 == dto.getIfManualBlock()) {
            // 会话升级，人工会话拦截，分配客服or留言，拉群
            if (0 == session.getState()) {
                // 已经拦截过了，开启转人工
                if (0 == dto.getIfManualBlock() && 0 == session.getBlockState()) {
                    // 拦截一次
                    handleManualBlock();
                } else {
                    // 开启会话升级
                    doUpgradeSession(dto, session, messageCallback);
                }
            }
            log.info("并发转人工，wait");
            return;
        }

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
