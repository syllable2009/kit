package com.jxp.hotline.handler.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.jxp.hotline.service.ManualService;
import com.jxp.hotline.service.RobotService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.service.TransferRuleService;
import com.jxp.hotline.utils.JedisUtils;
import com.jxp.hotline.utils.KsRedisCommands;

import cn.hutool.core.collection.CollUtil;
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
    @Resource
    private ManualService manualService;
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
//            cacheSession();
            synToAdmin();
        } else {
            // 设置会话消息，会话的lastId，时间信息并缓存等
            // 用户发的消息数加一，根据时间判断
            if (event.getTimestamp() < System.currentTimeMillis()) {
                // 更新信息
            }
            // 这里不做操作，都放在会话类型确定操作之后处理
        }

        // 如果处于人工会话中则直接发给人工
        if (StrUtil.equals("manual", activeSession.getSessionType())) {
            // 人工会话处理
            log.info("message handler,sendUserMessageToLiveCustomer,messageServerId:{},userId:{}", messageServerId,
                    userId);
            manualService.processUserMessage(activeSession, event);
            return;
        }
        // 否则进行转人工规则匹配，匹配到组
        final List<AssistantGroupInfo> assistantGroups = transferRuleService.matchLiveGroup(event);
        if (CollUtil.isEmpty(assistantGroups)) {
            //进入机器人会话
            robotService.processUserMessage(activeSession, event);
        } else if (1 == assistantGroups.size()) {
            // 开始转人工，分配客服
            distributeManualSession(event, activeSession.getSid(), assistantGroups.get(0), "userSend");
        } else {
            // 发送选择技能队列卡片，此时还是机器人会话，选择卡片以后调用distributeManualSession方法
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
            log.error("createNewSession return,lock fail,event:{}", JSONUtil.toJsonStr(event));
            return null;
        }
        // 创建对象，首先永远都是机器人
        final Long timestamp = event.getTimestamp();
        final LocalDateTime messageTime =
                Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        final SessionEntity newSession = SessionEntity.builder()
                .appId(event.getAppId())
                .robotId(event.getTo().getUserId())
                .sid(IdUtil.fastSimpleUUID())
                .sessionType("bot")
                .sessionState("botChat")
                .sessionFrom("userSend")
                .userFistMessageTime(messageTime)
                .userRequestRobotNum(0)
                .userRequestManualNum(0)
                .manulReplyNum(0)
                .createTime(messageTime)
                .updateTime(messageTime)
                .noRequest(true)
                .noReply(true)
                .build();
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
            return null;
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }

        // 同步会话，发事件来做
        synToAdmin();
        // 缓存会话信息
        return newSession;
    }

    private void sendUserChooseGroupMessage() {

    }

    private void distributeManualSession(MessageEvent event, String sessionId, AssistantGroupInfo groupInfo,
            String sessionFrom) {

        if (BooleanUtil.isFalse(groupInfo.getWorking())) {
            // 发送客服组不在工作时间留言消息
            log.info("distributeManualSession return,发送客服组不在工作时间留言消息,event:{}", JSONUtil.toJsonStr(event));
            return;
        }

        // 查询组内的客服列表
        List<AssistantInfo> assistantList = Lists.newArrayList();
        final List<AssistantInfo> onlineAssistantList = assistantList.stream()
                .filter(e -> StrUtil.equalsAny(e.getState(), "online", "busy"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(onlineAssistantList)) {
            log.info("distributeManualSession return,无客服在线,发送留言消息,event:{}", JSONUtil.toJsonStr(event));
            return;
        }

        // 加锁处理，如果有排队则一定进入排队
        // 如果没有排队，判断是否需要加入到排队中，不排队直接分配客服，分配不到进入排队
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, event.getAppId()
                , groupInfo.getGroupId());
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("distributeManualSession return,加锁失败,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        // 再去查一遍db
        final SessionEntity dbSession = sessionService.getSessionBySid(sessionId);
        try {
            if (null == dbSession) {
                // 会话恰好结束了，忽略掉，因为在会话管理算到这个会话了，会话结束的时候也要加锁
                log.info("distributeManualSession return,会话为空，数据异常,event:{}", JSONUtil.toJsonStr(event));
                return;
            }
            if (StrUtil.equals("manual", dbSession.getSessionType())) {
                // 出现并发，已经是人工了
                log.error("distributeManualSession return,并发转发到人工处理,event:{}", JSONUtil.toJsonStr(event));
                // 将此消息转发到人工处理
                manualService.processUserMessage(dbSession, event);
                return;
            }

            // 设置会话升级的来源
            dbSession.setSessionFrom(sessionFrom);

            final Integer queueNum = JedisUtils.getInt(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, event.getAppId(),
                    groupInfo.getGroupId()));

            // 如果有排队先进排队，因为分配客服是一个很重的操作
            boolean ifQueue = queueNum > 0;

            // 分配的客服
            AssistantInfo assistantInfo = null;

            // 不排队的话去分配客服
            if (!ifQueue) {
                assistantInfo = distributeAssistant(groupInfo, onlineAssistantList,
                        event.getAppId(), event.getFrom().getUserId());
                if (null == assistantInfo) {
                    // 没有分配到客服，去排队
                    // 创建排队session
                    ifQueue = true;
                }
            }
            if (ifQueue) {
                // 排队确认拦截
                if (BooleanUtil.isTrue(groupInfo.getIfEnableConfirm())) {
                    if (queueNum >= groupInfo.getConfirmNum()) {
                        log.info("distributeManualSession return,排队确认拦截,event:{}", JSONUtil.toJsonStr(event));
                        return;
                    }
                }
                // 取分配客服，如果分配到客服，客服全局会话数和客服本应用会话数已经+1了
                handleUpgradeQueueSession(groupInfo, queueNum, dbSession);
            } else {
                // 创建一个分配会话的session
                handleUpgradeManualSession(assistantInfo, dbSession);
            }

        } catch (Exception e) {
            // 控制incr操作后不能补偿的问题
            log.error("distributeManualSession lock exception, event:{},", JSONUtil.toJsonStr(event), e);
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
    }

    private void handleUpgradeManualSession(AssistantInfo assistantInfo, SessionEntity session) {
        session.setSessionState("muanualChat");
        session.setAssitantId(assistantInfo.getAssistantId());
        try {
            final Boolean ret = sessionService.upgradeManualSession(session);
            if (BooleanUtil.isFalse(ret)) {
                log.error("handleUpgradeManualSession return,升级为人工会话失败,session:{}", JSONUtil.toJsonStr(session));
                incrSessionNum(assistantInfo.getAppId(), assistantInfo.getAssistantId());
                return;
            }
        } catch (Exception e) {
            incrSessionNum(assistantInfo.getAppId(), assistantInfo.getAssistantId());
            log.error("handleUpgradeManualSession exception,session:{},", JSONUtil.toJsonStr(session), e);
        }
        // 发送MQ事件或者异步同步数据
        synToAdmin();
    }

    private void handleUpgradeQueueSession(AssistantGroupInfo groupInfo, Integer queueNum, SessionEntity dbSession) {
        dbSession.setSessionState("queue");
        // 有排队的，判断是否可以继续排队
        // 判断排队多少人，有没有设置排队过多不接单
        if (BooleanUtil.isTrue(groupInfo.getIfEnableConfirm())) {
            if (queueNum >= groupInfo.getConfirmNum()) {
                // 发送超过排队上限留言消息，此时还是机器人
                // 有时也需要触发排队确认转人工操作，此时点击回调distributeManualSession
                return;
            }
        }
        if (BooleanUtil.isTrue(groupInfo.getIfRejectManyQueue())) {
            if (queueNum > groupInfo.getNoticeNum()) {
                // 可以排队，发送排队安抚消息，排在第n位，请耐心等待
                return;
            }
        }

        final SessionEntity entity = SessionEntity.builder()
                .build();

        final Boolean ret = sessionService.upgradeQueueSession(entity);
        if (BooleanUtil.isFalse(ret)) {
            log.info("handleCreateQueueSession return,create session fail,session:{}", JSONUtil.toJsonStr(entity));
            return;
        }
        // 排队数+1
        final Integer incr = JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, event.getAppId(),
                groupInfo.getGroupId()));
        // 添加到排队列表中
        ksRedisCommands.lpush(SessionLockKey.format(SessionLockKey.AppGroupQueueList, event.getAppId()
                , groupInfo.getGroupId()), entity.getSid());
        // 发送MQ事件或者异步同步数据
    }

    private AssistantInfo distributeAssistant(AssistantGroupInfo groupInfo, List<AssistantInfo> manualInfoList, String appId,
            String userId) {
        // 可自动分配的客服列表,只有在线的客服才能自动分配分配
        final List<AssistantInfo> distributeAssistantList = manualInfoList.stream()
                .filter(e -> StrUtil.equals(e.getState(), "online"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(distributeAssistantList)) {
            // 发送当前无在线客服留言卡片
            return null;
        }
        return null;
    }

    private void incrSessionNum(String appId, String assitantId) {
        JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantGlobelSessionNum, assitantId));
        JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantAppSessionNum, appId,
                assitantId));
    }

    private void decrSessionNum(String appId, String assitantId) {
        JedisUtils.decr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantGlobelSessionNum, assitantId));
        JedisUtils.decr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantAppSessionNum, appId,
                assitantId));
    }

    // 消费客服上线事件，客服上线会打满直至饱和，用户的上线操作也会触发给其他在线客服分配
    private void fireAssistantOnline(String assitantId) {
        AssistantInfo assistantInfo = null;
        if (null == assistantInfo) {
            log.info("fireAssistantOnline return,assistantInfo is null,assitantId:{}", assitantId);
            return;
        }
        if (!StrUtil.equals("online", assistantInfo.getState())) {
            log.info("fireAssistantOnline return,assitantId not online,assistantInfo:{}", JSONUtil.toJsonStr(assistantInfo));
            return;
        }
        // 查询客服的所有配置组信息
        List<AssistantGroupInfo> groupInfoList = Lists.newLinkedList();
        for (AssistantGroupInfo group : groupInfoList) {
            fireDistributeAssistant(group.getAppId(), group.getGroupId());
        }
    }

    // 结束会话会触发事件，会触发自动分配的全局任务，所以与人没有关系，会在所有满足条件的用户中查找
    private void fireDistributeAssistant(String appId, String groupId) {
        // 加锁之前先判断一下，过滤数据，组的key不存在即为0
        if (ksRedisCommands.exists(SessionLockKey.format(SessionLockKey.AppGroupQueueNum, appId
                , groupId)) != 1) {
            log.error("fireDistributeAssistant return, queue num is 0", appId, groupId);
            return;
        }

        AssistantGroupInfo info = null;
        // 组信息为空
        if (null == info) {
            log.error("fireDistributeAssistant return, assistantGroupInfo is null", appId, groupId);
            return;
        }
        // 组不开启自动分配
        if (BooleanUtil.isFalse(info.getAutoDistribute())) {
            log.error("fireDistributeAssistant return, assistantGroupInfo is not autoDistribute", JSONUtil.toJsonStr(info));
            return;
        }

        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, appId
                , groupId);
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("fireDistributeAssistant lock fail, appId:{},groupId:{}", appId, groupId);
            return;
        }
        // 触发自动分配循环，直到一个结束条件：排队人数为0，或找不到客服
        try {
            String popKey = SessionLockKey.format(SessionLockKey.AppGroupQueueList, appId
                    , groupId);
            String queueNumKey = SessionLockKey.format(SessionLockKey.AppGroupQueueNum, appId
                    , groupId);
            String assistantId = null;
            while (true) {
                final Object lindex = ksRedisCommands.lindex(popKey, 0);
                if (null == lindex) {
                    ksRedisCommands.del(popKey, queueNumKey);
                    return;
                }
                // 分配客服
                assistantId = null;
                if (StrUtil.isBlank(assistantId)) {
                    // 没有分配到客服，客服打满了
                    // TODO 客服自己上限的原因才停，单应用下应该继续尝试分配其他应用
                    return;
                } else {
                    // 移除队列
                    ksRedisCommands.lpush(popKey);
                    // 排队数原子性减一
                    ksRedisCommands.decr(queueNumKey);
                }
            }
        } catch (Exception e) {
            // 控制incr操作后不能补偿的问题
            log.error("fireDistributeAssistant lock exception, appId:{},groupId:{},", appId, groupId, e);
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
    }

    private void synToAdmin() {

    }
}
