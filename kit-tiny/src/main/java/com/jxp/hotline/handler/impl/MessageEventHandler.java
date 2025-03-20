package com.jxp.hotline.handler.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.constant.SessionLockKey;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.handler.EventHandler;
import com.jxp.hotline.service.RobotService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.service.TransferRuleService;
import com.jxp.hotline.utils.JedisUtils;
import com.jxp.hotline.utils.KsRedisCommands;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:15
 */
@Slf4j
@EventType("message")
public class MessageEventHandler implements EventHandler {

    @Resource
    private SessionService sessionService;
    @Resource
    private TransferRuleService transferRuleService;
    @Resource
    private RobotService robotService;
    @Autowired(required = false)
    private KsRedisCommands ksRedisCommands;

    @Override
    public void handle(MessageEvent event) {
        log.info("message handler,event:{}", JSONUtil.toJsonStr(event));
        final String messageServerId = event.getAppId();
        final String userId = event.getFrom().getUserId();
        // 获取该应用下唯一的一个在线会话
        SessionEntity activeSession = sessionService.getActiveSession(messageServerId, userId);
        if (null == activeSession) {
            // 没有会话，需要加锁创建会话
            log.info("message handler,create new robot session,messageServerId:{},userId:{}", messageServerId,
                    userId);
            activeSession = createNewSession(event);
            // 没有创建成功会话直接返回
            if (null == activeSession) {
                log.error("message handler return,no found active session,messageServerId:{},userId:{}",
                        messageServerId,
                        userId);
                return;
            }
        } else {
            // 设置会话消息，会话的lastId，时间信息并缓存等
        }

        // 如果处于人工会话中则
        if (StrUtil.equals("manual", activeSession.getSessionType())) {
            // 人工会话处理
            log.info("message handler,sendUserMessageToLiveCustomer,messageServerId:{},userId:{}", messageServerId,
                    userId);
            sendUserMessageToLiveCustomer(event);
            return;
        }
        // 否则没有会话
        final List<AssistantGroupInfo> assistantGroups = transferRuleService.matchLiveGroup(event);
        if (CollUtil.isEmpty(assistantGroups)) {
            //进入机器人会话
            robotService.processUserMessage(event);
        } else if (1 == assistantGroups.size()) {
            // 开始转人工，分配客服
            distributeManualSession();
        } else {
            // 发送选择技能队列卡片
            sendUserChooseGroupMessage();
        }
    }

    @Override
    public String getName() {
        return "MessageEventHandler";
    }

    private SessionEntity createNewSession(MessageEvent event) {
        // 加锁：需要和升级会话加锁的key一致
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionLockKey, event.getAppId()
                , event.getFrom().getUserId());

        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("createNewSession lock fail, event:{}", JSONUtil.toJsonStr(event));
            return null;
        }
        // 创建对象
        final SessionEntity newSession = SessionEntity.builder().build();
        try {
            // 再去查一遍db
            final SessionEntity activeSession = sessionService.getActiveSession(event.getAppId(), event.getFrom().getUserId());
            if (null != activeSession) {
                return activeSession;
            }

            final Boolean ret = sessionService.createSession(newSession);
            if (BooleanUtil.isFalse(ret)) {
                log.error("createNewSession return,createSession fail,messageServerId:{},userId:{}", JSONUtil.toJsonStr(newSession));
                return null;
            }
        } catch (Exception e) {
            log.error("createNewSession lock exception, event:{},", JSONUtil.toJsonStr(event), e);

        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }

        // 同步会话，发事件来做
        // 缓存会话信息
        return newSession;
    }

    private void sendUserMessageToLiveCustomer(MessageEvent event) {
    }

    private void sendUserChooseGroupMessage() {

    }

    private void distributeManualSession(MessageEvent event, SessionEntity activeSession, AssistantGroupInfo groupInfo) {

        if (BooleanUtil.isFalse(groupInfo.getWorking())) {
            // 发送客服组不在工作时间留言卡片
            return;
        }
        // 判断排队多少人，有没有设置排队过多不接单
        if (BooleanUtil.isTrue(groupInfo.getIfRejectManyQueue())) {
            if (0 > groupInfo.getQueueNum()) {
                // 发送超过排队上限留言消息
                return;
            }
        }

        // 查询所有的组
        List<AssistantInfo> assistantList = Lists.newLinkedList();

        // 只有在线的客服才能分配
        final List<AssistantInfo> onlineAssistantList = assistantList.stream()
                .filter(e -> StrUtil.equals("online", e.getState()))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(onlineAssistantList)) {
            // 发送当前无在线客服留言卡片
            return;
        }

        final AssistantInfo assistantInfo = distributeAssistant(groupInfo, onlineAssistantList, );
        boolean ifQueue = null == assistantInfo;
        String assistantId = null;
        if (ifQueue) {
            // 排队加一
            final Integer incr = JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, event.getAppId(),
                    groupInfo.getGroupId()));

        } else {
            assistantId = assistantInfo.getAssistantId();
        }

        // 会话升级
        // 加锁：需要和升级会话加锁的key一致
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionLockKey, event.getAppId()
                , event.getFrom().getUserId());
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("distributeManualSession return,lock fail, event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        try {
            // 再去查一遍db
            final SessionEntity dbSession = sessionService.getSessionBySid(activeSession.getSid());
            if (null == dbSession) {
                // 异常了
                log.error("distributeManualSession return,dbSession is null, event:{}", JSONUtil.toJsonStr(event));
                return;
            }
            if (StrUtil.equals("manual", dbSession.getSessionType())) {
                // 出现并发，已经是人工了
                return;
            }

            // 更新会话的状态，以此为主判断会话状态，同步失败需要有补偿机制

        } catch (Exception e) {
            log.error("distributeManualSession return,lock exception, event:{},", JSONUtil.toJsonStr(event), e);
            // 补偿会话数据
            return;
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }

        // 会话变成人工了
        // 各种会话初始化
        if (ifQueue) {
            // 排队会话
        } else {
            // 自动进入
        }
    }

    private AssistantInfo distributeAssistant(AssistantGroupInfo groupInfo, List<AssistantInfo> manualInfoList, String appId,
            String userId) {
        return null;
    }
}
