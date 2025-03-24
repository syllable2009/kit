package com.jxp.hotline.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.jxp.hotline.constant.SessionLockKey;
import com.jxp.hotline.domain.dto.DistributeAssitant;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.JedisUtils;
import com.jxp.hotline.utils.KsRedisCommands;
import com.jxp.hotline.utils.LocalDateTimeUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

@Slf4j
public abstract class DefaultSessionManageService implements SessionManageService {

    @Override
    public List<AssistantGroupInfo> matchLiveGroup(MessageEvent event) {
        return null;
    }

    @Resource
    private SessionService sessionService;
    @Autowired(required = false)
    private KsRedisCommands ksRedisCommands;

    @Override
    public SessionEntity getLastActiveSession(String appId, String userId) {
        return sessionService.getActiveSession(appId, userId);
    }

    private SessionEntity createNewCommonSession(MessageEvent event) {
        final String appId = event.getAppId();
        // 客服发给app还是用户发给app
        final String userId = event.getFrom().getUserId();

        // 加锁：需要和升级会话加锁的key一致
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionLockKey, appId
                , userId);

        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("createNewSession return,lock fail,event:{}", JSONUtil.toJsonStr(event));
            return null;
        }
        final String sessionType = event.getSessionType();
        boolean ifUserSend = StrUtil.equals("p2p", sessionType);
        final String messageKey = event.getInfo().getMessageKey();
        // 创建对象，首先永远都是机器人
        final Long timestamp = event.getTimestamp();
        final LocalDateTime messageTime = LocalDateTimeUtil.timestampToLocalDateTime(timestamp);
        SessionEntity newSession = SessionEntity.builder()
                .appId(event.getAppId())
                .robotId(event.getTo().getUserId())
                .sid(IdUtil.fastSimpleUUID())
                .userRequestManualNum(0)
                .createTime(messageTime)
                .updateTime(messageTime)
                .noRequest(true)
                .noReply(true)
                .sessionFirstMessageId(messageKey)
                .build();

        if (ifUserSend) {
            newSession.setSessionFrom("userToManual");
            newSession.setSessionType("bot");
            newSession.setSessionState("botChat");
            newSession.setUserRequestRobotNum(1);
            newSession.setUserFistMessageId(messageKey);
            newSession.setUserFistMessageTime(messageTime);
        } else {
            newSession.setSessionFrom("manualToUser");
            newSession.setSessionType("manual");
            newSession.setSessionState("muanualChat");
            newSession.setNoReply(false);
            newSession.setManulReplyNum(1);
            newSession.setManulFirstMessageId(messageKey);
            newSession.setManulFirstMessageTime(messageTime);
        }
        try {
            // 再去查一遍db
            final SessionEntity activeSession = this.getLastActiveSession(event.getAppId(),
                    userId);
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
        return newSession;
    }

    @Override
    public SessionEntity createNewSession(MessageEvent event) {
        final SessionEntity newRobotSession = createNewCommonSession(event);
        if (null != newRobotSession) {
            doAfterSessionCreate(newRobotSession);
        }
        return newRobotSession;
    }

    @Override
    public void recordUserLastMessage(SessionEntity session, MessageEvent event) {
        // 设置会话消息，会话的lastId，时间信息并缓存等
        // 用户发的消息数加一，根据时间判断，可以和最后一条消息来判断
        if (event.getTimestamp() < System.currentTimeMillis()) {
            // 更新信息
            final String userLastMessageId = session.getUserLastMessageId();
            final LocalDateTime userLastMessageTime = session.getUserLastMessageTime();
            final Integer userRequestRobotNum = session.getUserRequestRobotNum();
            final Integer userRequestManualNum = session.getUserRequestManualNum();
        }
        // 根据用户的会话状态更新不同的字段

    }

    @Override
    public void recordManualLastMessage(SessionEntity session, String messageKey, LocalDateTime messageTime) {

    }

    public void doAfterSessionCreate(SessionEntity session) {
        // 同步会话，发事件来做
//        cacheSession();
//        synToAdmin();
        // 缓存会话信息
    }

    @Override
    public void endSession(SessionEntity session) {
        sessionService.endSession(session);
        // 结束掉会话
        doAfterSessionEnd(session);
    }

    public void doAfterSessionEnd(SessionEntity session) {
        // 同步给三方
        synToAdmin();
        // 清掉缓存
        if (StrUtil.equals("manual", session.getSessionType())) {
            // 结束人工会话，自动分配
            handleManualSessionEndEvent(session);
        } else {
            // 结束机器人会话
        }
    }

    @Override
    public Boolean forwardManualSession(SessionEntity session, String assistantId) {
        AssistantInfo assistantInfo = null;
        if (null == assistantInfo) {
            log.info("doAppointDistributeAssistant return, assistantInfo is null");
            return false;
        }
        // 如果转接到人，客服人员必须要在线
        AssistantGroupInfo groupInfo = null;

        // 结束老会话
        Boolean ifEndOldSession = false;
        if (BooleanUtil.isFalse(ifEndOldSession)) {
            return false;
        }
        // 给指定人分配会话
        incrSessionNum(session.getTargetId(), assistantInfo.getAssistantId());
        session.setAssitantId(assistantInfo.getAssistantId());
        final Boolean ret = sessionService.createSession(session);
        if (BooleanUtil.isFalse(ret)) {
            decrSessionNum(session.getTargetId(), assistantInfo.getAssistantId());
            return false;
        } else {
            synToAdmin();
        }
        return true;
    }

    private void tryDistributeManualSession(MessageEvent event, SessionEntity session, AssistantGroupInfo groupInfo,
            String sessionFrom) {

        if (BooleanUtil.isFalse(groupInfo.getWorking())) {
            // 发送客服组不在工作时间留言消息
            log.info("tryDistributeManualSession return,发送客服组不在工作时间留言消息,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        // 查询组内的客服列表
        List<AssistantInfo> assistantList = Lists.newArrayList();
        final List<AssistantInfo> onlineAssistantList = assistantList.stream()
                .filter(e -> StrUtil.equalsAny(e.getState(), "online", "busy"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(onlineAssistantList)) {
            log.info("tryDistributeManualSession return,无客服在线,发送留言消息,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        // 加锁处理，如果有排队则一定进入排队
        // 如果没有排队，判断是否需要加入到排队中，不排队直接分配客服，分配不到进入排队
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, event.getAppId()
                , groupInfo.getGroupId());
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("tryDistributeManualSession return,加锁失败,event:{}", JSONUtil.toJsonStr(event));
            return;
        }
        // 再去查一遍db
        final SessionEntity dbSession = sessionService.getSessionBySid(session.getSid());
        try {
            if (null == dbSession) {
                // 会话恰好结束了，忽略掉，因为在会话管理算到这个会话了，会话结束的时候也要加锁
                log.info("tryDistributeManualSession return,会话为空，数据异常,event:{}", JSONUtil.toJsonStr(event));
                return;
            }
            // 会话校验
            if (StrUtil.equals("manual", dbSession.getSessionType())) {
                // 出现并发，已经是人工了
                log.error("tryDistributeManualSession return,并发转发到人工处理,event:{}", JSONUtil.toJsonStr(event));
                // 将此消息转发到人工处理
//                manualService.processUserMessage(dbSession, event);
                return;
            }

            // 设置会话升级的来源
            dbSession.setSessionFrom(sessionFrom);

            final Integer queueNum = JedisUtils.getInt(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, event.getAppId(),
                    groupInfo.getGroupId()));

            // 如果有排队先进排队，因为分配客服是一个比较重的操作
            boolean ifNeedQueue = queueNum > 0;
            // 分配信息
            DistributeAssitant distribute = null;
            // 不排队的话去尝试分配客服
            if (!ifNeedQueue) {
                distribute = distributeAssistant(groupInfo, onlineAssistantList, dbSession);
                if (BooleanUtil.isFalse(distribute.getDistributeResult())) {
                    // 没有分配到客服，去排队
                    // 创建排队session
                    ifNeedQueue = true;
                }
            }
            if (ifNeedQueue) {
                // 排队拦截
                if (interceptQueueSession(groupInfo, queueNum)) {
                    log.info("tryDistributeManualSession return,排队拦截处理,event:{}", JSONUtil.toJsonStr(event));
                    return;
                }
                // 取分配客服，如果分配到客服，客服全局会话数和客服本应用会话数已经+1了
                handleUpgradeQueueSession(groupInfo, queueNum, dbSession);
            } else {
                // 创建一个分配会话的session
                handleUpgradeManualSession(groupInfo, distribute.getAssistantInfo(), dbSession);
            }

        } catch (Exception e) {
            // 控制incr操作后不能补偿的问题
            log.error("tryDistributeManualSession lock exception, event:{},", JSONUtil.toJsonStr(event), e);
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
    }

    // 分配会话，包装分配的结果和分配的详细信息
    private DistributeAssitant distributeAssistant(AssistantGroupInfo groupInfo, List<AssistantInfo> manualInfoList,
            SessionEntity session) {

        final String appId = groupInfo.getAppId();
        final String groupId = groupInfo.getGroupId();

        // 组不开启自动分配
        if (BooleanUtil.isFalse(groupInfo.getAutoDistribute())) {
            log.info("doGroupDistributeAssistant return, assistantGroupInfo is not autoDistribute,appId:{},groupId:{}"
                    , appId, groupId);
            return generateFailDistributeAssitant("groupCloseAutoDistribute");
        }

        // 组的排队数校验
        final String groupQueueNumKey = SessionLockKey.format(SessionLockKey.AppGroupQueueNum, appId, groupId);
        final Integer groupQueueNum = JedisUtils.getInt(ksRedisCommands, groupQueueNumKey);
        if (groupQueueNum < 1) {
            log.info("doGroupDistributeAssistant return, queue num is 0,appId:{},groupId:{}", appId, groupId);
            return generateFailDistributeAssitant("groupQueueNull");
        }

        // 可自动分配的客服列表,只有在线的客服才能自动分配分配
        final List<AssistantInfo> distributeAssistantList = manualInfoList.stream()
                .filter(e -> StrUtil.equals(e.getState(), "online"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(distributeAssistantList)) {
            return generateFailDistributeAssitant("onNolineAssistant");
        }
        // 是否开始最近优先分配
        if (BooleanUtil.isTrue(groupInfo.getIfDistributeRecently())) {
            // 查询最近的会话，判断是否可分配列表中
            AssistantInfo assistant = null;
            if (null != assistant) {
                return DistributeAssitant.builder()
                        .distributeResult(true)
                        .distributeStrategy("recently")
                        .build();
            }
        }
        // 分配的客服
        DistributeAssitant distribute = null;
        final String distributeStrategy = groupInfo.getDistributeStrategy();
        switch (distributeStrategy) {
            case "saturation":
                distribute = getLongestAssistant(groupInfo.getGroupId(), manualInfoList);
                break;
            case "longest":
                distribute = getLongestAssistant(groupInfo.getGroupId(), manualInfoList);
                break;
            case "workload":
                distribute = getLongestAssistant(groupInfo.getGroupId(), manualInfoList);
                break;
            default:
                return generateFailDistributeAssitant("notSupportDistributeStrategy");
        }
        distribute.setDistributeStrategy(distributeStrategy);
        return distribute;
    }

    private static DistributeAssitant generateFailDistributeAssitant(String reason) {
        return DistributeAssitant.builder()
                .distributeResult(false)
                .failReason(reason)
                .build();
    }

    private DistributeAssitant getLongestAssistant(String groupId, List<AssistantInfo> manualInfoList) {

        String assistantId = null;
        Integer maxGlobalCount = null;
        Integer maxAppCount = null;

        DistributeAssitant distribute = null;

        // 按照时间顺序对list重排
        for (AssistantInfo a : manualInfoList) {
            assistantId = a.getAssistantId();
            maxGlobalCount = a.getMaxGlobalCount();
            // 如果全局+1成功，单应用失败，则在单应用校验中补偿
            if (hasReachedGlobalMax(assistantId, maxGlobalCount)) {
                distribute = generateFailDistributeAssitant("reachedGlobalMax");
                continue;
            }
            maxAppCount = a.getMaxAppCount();
            if (hasReachedAppMax(groupId, assistantId, maxAppCount)) {
                distribute = generateFailDistributeAssitant("reachedAppMax");
                continue;
            }
            return DistributeAssitant.builder()
                    .assistantInfo(a)
                    .distributeResult(true)
                    .build();
        }
        return distribute;
    }

    private boolean hasReachedGlobalMax(String assistantId, Integer maxNum) {
        // 给组加锁了，这里就不给人加锁了，利用incr和decr的原子性操作2次
        final String key = SessionLockKey.format(SessionLockKey.AssitantGlobelSessionNum, assistantId);
        final int globelNum = JedisUtils.incr(ksRedisCommands, key).intValue();
        if (globelNum > maxNum) {
            // 补偿回来
            JedisUtils.decr(ksRedisCommands, key);
            return true;
        }
        return false;
    }

    private boolean hasReachedAppMax(String groupId, String assistantId, Integer maxNum) {
        final String key = SessionLockKey.format(SessionLockKey.AssitantAppSessionNum, groupId, assistantId);
        final int globelNum = JedisUtils.incr(ksRedisCommands, key).intValue();
        if (globelNum > maxNum) {
            // 双补偿
            decrSessionNum(groupId, assistantId);
            return true;
        }
        return false;
    }

    private void incrSessionNum(String groupId, String assitantId) {
        JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantGlobelSessionNum, assitantId));
        JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantAppSessionNum, groupId,
                assitantId));
    }

    // 其他地方不补偿，除非创建会话db失败
    private void decrSessionNum(String groupId, String assitantId) {
        JedisUtils.decr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantGlobelSessionNum, assitantId));
        JedisUtils.decr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AssitantAppSessionNum, groupId,
                assitantId));
    }

    // 排队拦截
    private boolean interceptQueueSession(AssistantGroupInfo groupInfo, Integer queueNum) {
        // 判断排队多少人，有没有设置排队过多不接单
        if (BooleanUtil.isTrue(groupInfo.getIfRejectManyQueue())) {
            if (queueNum >= groupInfo.getRejectQueueNum()) {
                // 发送超过排队上限留言消息，此时还是机器人
                log.info("distributeManualSession return,发送超过排队上限留言消息,queueNum:{}", queueNum);
                return true;
            }
        }
        // 排队确认拦截
        if (BooleanUtil.isTrue(groupInfo.getIfEnableConfirm())) {
            if (queueNum >= groupInfo.getConfirmNum()) {
                // 有时也需要触发排队确认转人工操作，此时点击回调tryDistributeManualSession
                log.info("distributeManualSession return,排队确认转人工操作,queueNum:{}", queueNum);
                return true;
            }
        }
        return true;
    }

    // 升级操作了，理论上已经算是人工了
    private void handleUpgradeQueueSession(AssistantGroupInfo groupInfo, Integer queueNum, SessionEntity dbSession) {

        // 本次要操作的对象，不要修改参数对象
        final SessionEntity entity = SessionEntity.builder()
                .sid(dbSession.getSid())
                .sessionState("queue")
                .targetType("groupTag")
                .targetId(groupInfo.getGroupId())
                .build();

        final Boolean ret = sessionService.upgradeQueueSession(entity);
        if (BooleanUtil.isFalse(ret)) {
            log.info("handleCreateQueueSession return,create session fail,session:{}", JSONUtil.toJsonStr(entity));
            return;
        }
        // 排队数+1
        final Integer incr = JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, dbSession.getAppId(),
                groupInfo.getGroupId()));
        // 添加到排队列表中
        ksRedisCommands.lpush(SessionLockKey.format(SessionLockKey.AppGroupQueueList, dbSession.getAppId()
                , groupInfo.getGroupId()), entity.getSid());

        // 排队通知消息
        if (BooleanUtil.isTrue(groupInfo.getIfNoticeManyQueue())) {
            if (queueNum > groupInfo.getNoticeNum()) {
                // 可以排队，发送排队安抚消息，排在第n位，请耐心等待
            }
        }
        // 发送MQ事件或者异步同步数据
    }

    private void handleUpgradeManualSession(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo,
            SessionEntity session) {

        // 转人工拦截
        final SessionEntity entity = SessionEntity.builder()
                .sid(session.getSid())
                .sessionState("muanualChat")
                .assitantId(assistantInfo.getAssistantId())
                .targetType("groupTag")
                .targetId(groupInfo.getGroupId())
                .build();
        try {
            final Boolean ret = sessionService.upgradeManualSession(entity);
            if (BooleanUtil.isFalse(ret)) {
                log.error("handleUpgradeManualSession return,升级为人工会话失败,session:{}", JSONUtil.toJsonStr(session));
                incrSessionNum(assistantInfo.getAppId(), assistantInfo.getAssistantId());
                return;
            }
        } catch (Exception e) {
            incrSessionNum(assistantInfo.getAppId(), assistantInfo.getAssistantId());
            log.error("handleUpgradeManualSession exception,session:{},", JSONUtil.toJsonStr(session), e);
        }
        // 发送转人工系统消息
        // 发送MQ事件或者异步同步数据
        synToAdmin();
    }

    // 会话管理
    private void synToAdmin() {

    }

    private void cacheSession() {

    }


    @Override
    public DistributeAssitant doGroupDistributeAssistant(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo) {

        if (null == groupInfo) {
            log.info("doGroupDistributeAssistant return, groupInfo is null,appId:{},groupId:{}");
            return generateFailDistributeAssitant("groupInfoError");
        }
        String appId = groupInfo.getAppId();
        String groupId = groupInfo.getGroupId();
        // 组不开启自动分配
        if (BooleanUtil.isFalse(groupInfo.getAutoDistribute())) {
            log.info("doGroupDistributeAssistant return, assistantGroupInfo is not autoDistribute,appId:{},groupId:{}"
                    , appId, groupId);
            return generateFailDistributeAssitant("groupCloseAutoDistribute");
        }

        // 组维度的加锁，尝试自动分配
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, appId
                , groupId);
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("doGroupDistributeAssistant lock fail, appId:{},groupId:{}", appId, groupId);
            return generateFailDistributeAssitant("groupLockFail");
        }

        // 分配的客服，分配不到位空
        DistributeAssitant distribute = null;
        try {
            final String popKey = SessionLockKey.format(SessionLockKey.AppGroupQueueList, appId
                    , groupId);
            final String groupQueueNumKey = SessionLockKey.format(SessionLockKey.AppGroupQueueNum, appId, groupId);
            final Object lindex = ksRedisCommands.lindex(popKey, 0);
            if (null == lindex) {
                // 清空排队key和统计数，加锁实现
                ksRedisCommands.del(popKey, groupQueueNumKey);
                log.info("doGroupDistributeAssistant return, queue is empty,appId:{},groupId:{}", appId, groupId);
                return generateFailDistributeAssitant("groupQueueEmpty");
            }
            // 要分配的会话id
            String sessionId = lindex.toString();
            // 获取到会话信息，模拟查询
            SessionEntity session = new SessionEntity();
            session.setSid(sessionId);

            // 查询组内的客服列表，找到在线客服
            List<AssistantInfo> assistantList = null;
            if (null == assistantInfo) {
                assistantList = Lists.newArrayList();
            } else {
                assistantList = Lists.newArrayList(assistantInfo);
            }
            // 此时只需要给特定的客服分配
            distribute = distributeAssistant(groupInfo, assistantList, session);
            if (BooleanUtil.isTrue(distribute.getDistributeResult())) {
                // 移除队列
                ksRedisCommands.lpop(popKey);
                // 排队数原子性减一
                ksRedisCommands.decr(groupQueueNumKey);
            }
        } catch (Exception e) {
            log.error("doGroupDistributeAssistant lock exception, appId:{},groupId:{},", appId, groupId, e);
            distribute = DistributeAssitant.builder()
                    .distributeResult(false)
                    .failReason(e.getMessage())
                    .build();
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
        if (null == distribute) {
            distribute = DistributeAssitant.builder()
                    .distributeResult(false)
                    .failReason("unkownReason")
                    .build();
        }
        return distribute;
    }

    @Override
    public void tryDistributeManualSession(SessionEntity session, AssistantGroupInfo groupInfo,
            String sessionFrom) {
        if (BooleanUtil.isFalse(groupInfo.getWorking())) {
            // 发送客服组不在工作时间留言消息
            log.info("tryDistributeManualSession return,发送客服组不在工作时间留言消息,session:{}", JSONUtil.toJsonStr(session));
            return;
        }
        // 查询组内的客服列表
        List<AssistantInfo> assistantList = Lists.newArrayList();
        final List<AssistantInfo> onlineAssistantList = assistantList.stream()
                .filter(e -> StrUtil.equalsAny(e.getState(), "online", "busy"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(onlineAssistantList)) {
            log.info("tryDistributeManualSession return,无客服在线,发送留言消息,session:{}", JSONUtil.toJsonStr(session));
            return;
        }

        // 加锁处理，如果有排队则一定进入排队
        // 如果没有排队，判断是否需要加入到排队中，不排队直接分配客服，分配不到进入排队
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, session.getAppId()
                , groupInfo.getGroupId());
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("tryDistributeManualSession return,加锁失败,session:{}", JSONUtil.toJsonStr(session));
            return;
        }

        // 再去查一遍db
        final SessionEntity dbSession = sessionService.getSessionBySid(session.getSid());
        try {
            if (null == dbSession) {
                // 会话恰好结束了，忽略掉，因为在会话管理算到这个会话了，会话结束的时候也要加锁
                log.info("tryDistributeManualSession return,会话为空，数据异常,session:{}", JSONUtil.toJsonStr(session));
                return;
            }
            if (StrUtil.equals("manual", dbSession.getSessionType())) {
                // 出现并发，已经是人工了
                log.info("tryDistributeManualSession return,并发转发到人工处理,session:{}", JSONUtil.toJsonStr(dbSession));
                // 已经转人工了，直接发送给人工
                // TODO 如何发送原消息呢
                return;
            }

            // 设置会话升级的来源
            dbSession.setSessionFrom(sessionFrom);

            final Integer queueNum = JedisUtils.getInt(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, dbSession.getAppId(),
                    groupInfo.getGroupId()));

            // 如果有排队先进排队，因为分配客服是一个比较重的操作
            boolean ifNeedQueue = queueNum > 0;
            // 分配信息
            DistributeAssitant distribute = null;
            // 不排队的话去尝试分配客服
            if (!ifNeedQueue) {
                distribute = distributeAssistant(groupInfo, onlineAssistantList, dbSession);
                if (BooleanUtil.isFalse(distribute.getDistributeResult())) {
                    // 没有分配到客服，去排队
                    // 创建排队session
                    ifNeedQueue = true;
                }
            }
            if (ifNeedQueue) {
                // 排队拦截
                if (interceptQueueSession(groupInfo, queueNum)) {
                    log.info("tryDistributeManualSession return,排队拦截处理,session:{}", JSONUtil.toJsonStr(dbSession));
                    return;
                }
                // 未分配到客服，排队会话
                handleUpgradeQueueSession(groupInfo, queueNum, dbSession);
            } else {
                // 分配到客服，客服全局会话数和客服本应用会话数已经+1了
                handleUpgradeManualSession(groupInfo, distribute.getAssistantInfo(), dbSession);
            }

        } catch (Exception e) {
            // 控制incr操作后不能补偿的问题
            log.error("tryDistributeManualSession lock exception, session:{},", JSONUtil.toJsonStr(dbSession), e);
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
    }

    @Override
    public void distributeQueueSession(SessionEntity session) {
        final Boolean ret = sessionService.distributeSession(session);
        if (BooleanUtil.isFalse(ret)) {
            // 操作失败补偿
            final String appId = session.getAppId();
            final String groupId = session.getTargetId();
            final String groupQueueNumKey = SessionLockKey.format(SessionLockKey.AppGroupQueueNum, appId, groupId);
            ksRedisCommands.incr(groupQueueNumKey);
            final String popKey = SessionLockKey.format(SessionLockKey.AppGroupQueueList, appId
                    , groupId);
            ksRedisCommands.rpush(popKey, session.getSid());
            // 排队的数据回滚
            incrSessionNum(groupId, session.getAssitantId());
        } else {
            // 发送事件或异步处理
            synToAdmin();
        }
    }

    // 处理会话结束自动分配事件，转接给别人相当于自己的会话结束，别人可以直接分配
    @Override
    public void handleManualSessionEndEvent(SessionEntity dbSession) {
        // 这个会话结束了
        AssistantGroupInfo groupInfo = null;
        AssistantInfo assistantInfo = null;
        DistributeAssitant distribute = this.doGroupDistributeAssistant(groupInfo, assistantInfo);
        if (BooleanUtil.isTrue(distribute.getDistributeResult())) {
            // 分到的新会话
            final String sid = distribute.getSessionId();
            SessionEntity newSession = SessionEntity.builder().build();
            if (null == newSession) {

            }
            // 查询新会话
            this.distributeQueueSession(SessionEntity.builder()
                    .sid(sid)
                    .appId(newSession.getAppId())
                    .targetType(newSession.getTargetType())
                    .targetId(newSession.getTargetId())
                    .assitantId(distribute.getAssistantInfo().getAssistantId())
                    .build());
        }
    }

    @Override
    public void handleForwardSessionEvent(String sessionId, String assitantId) {
        SessionEntity dbSession = null;
        this.forwardManualSession(dbSession, assitantId);
    }

    @Override
    // 消费客服上线事件，客服上线会打满直至饱和，用户的上线操作也会触发给其他在线客服分配
    public void handleAssitantOnlineEvent(String assitantId) {
        // 查询客服信息
        AssistantInfo assistantInfo = null;
        if (null == assistantInfo) {
            log.info("handleAssitantOnlineEvent return,assistantInfo is null,assitantId:{}", assitantId);
            return;
        }
        if (!StrUtil.equals("online", assistantInfo.getState())) {
            log.info("handleAssitantOnlineEvent return,assitantId not online,assistantInfo:{}", JSONUtil.toJsonStr(assistantInfo));
            return;
        }
        // 查询客服的所有组信息
        List<AssistantGroupInfo> groupInfoList = Lists.newLinkedList();
        DistributeAssitant distribute = null;
        for (AssistantGroupInfo group : groupInfoList) {
            distribute = this.doGroupDistributeAssistant(group, assistantInfo);
            // 达到全局上限后退出循环
            if (BooleanUtil.isFalse(distribute.getDistributeResult()) && StrUtil.equals("globelMax",
                    distribute.getFailReason())) {
                break;
            }
            if (BooleanUtil.isTrue(distribute.getDistributeResult())) {
                // 分到的新会话
                final String sessionId = distribute.getSessionId();
                // 查询新会话
                SessionEntity newSession = SessionEntity.builder()
                        .sid(sessionId)
                        .build();
                this.distributeQueueSession(SessionEntity.builder()
                        .sid(newSession.getSid())
                        .appId(newSession.getAppId())
                        .targetType(newSession.getTargetType())
                        .targetId(newSession.getTargetId())
                        .assitantId(distribute.getAssistantInfo().getAssistantId())
                        .build());
            }
        }
    }

    @Override
    public void processUserMessageToAppEvent(SessionEntity session, MessageEvent event) {
        recordUserLastMessage(session, event);
//        doAfterUserLastMessage();
    }

    @Override
    public void processManualMessageToUserEvent(SessionEntity session, MessageEvent event) {

    }

    @Override
    public void processNoticeMessageToUserEvent(SessionEntity session, String templateId, Map<String, String> paramId) {

    }

    @Override
    public void processMixcardMessageToUserEvent(SessionEntity session, String templateId, Map<String, String> paramId) {

    }
}
