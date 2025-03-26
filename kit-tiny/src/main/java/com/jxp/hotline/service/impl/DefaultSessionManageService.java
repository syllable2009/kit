package com.jxp.hotline.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.jxp.hotline.constant.SessionLockKey;
import com.jxp.hotline.domain.dto.BotConfig;
import com.jxp.hotline.domain.dto.CustomerGroupDTO;
import com.jxp.hotline.domain.dto.DistributeAssitant;
import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.dto.TransferRuleDTO;
import com.jxp.hotline.domain.dto.TransferRuleItemDTO;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.domain.entity.SessionEntity.SessionEntityBuilder;
import com.jxp.hotline.service.MessageService;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;
import com.jxp.hotline.utils.JedisUtils;
import com.jxp.hotline.utils.KsRedisCommands;
import com.jxp.hotline.utils.LocalDateTimeUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-24 14:32
 */

@Slf4j
public abstract class DefaultSessionManageService implements SessionManageService {

    @Resource
    private MessageService messageService;

    @Override
    public List<CustomerGroupDTO> matchLiveGroup(MessageEvent event) {
        // 匹配的规则有权重，只会找到其中一个匹配的规则
        final String appId = event.getAppId();
        // 获取应用下的转人工规则
        String redisKey = SessionLockKey.format(SessionLockKey.appTransferRule, appId);
        String ruleString = "[]";
        if (StrUtil.isBlank(ruleString)) {
            return Lists.newArrayList();
        }
        final List<TransferRuleDTO> ruleList = JSONUtil.toList(ruleString, TransferRuleDTO.class);
        if (CollUtil.isEmpty(ruleList)) {
            return Lists.newArrayList();
        }

        // 解析用户的文本输入
        String userInputText = "";
        final String userId = event.getFrom().getUserId();
        boolean matchResult = false;
        // 按照权重找到匹配的一个即可
        for (TransferRuleDTO rule : ruleList) {
            // 每个规则下有许多条件组，单个条件组内是and的关系，多个组之间是or的关系
            matchResult = rule.getTriggerConditionList().stream()
                    .anyMatch(triggerList -> triggerList.stream()
                            .allMatch(e -> matchCustomRule(e, appId, userId, userInputText)));
            if (matchResult) {
                return StrUtil.equals("allGroup", rule.getCustomerGroupType()) ? rule.getCustomerGroupList() : rule.getCustomerGroupList();
            }
        }
        return Lists.newArrayList();
    }

    // 底层使用的信息，采用全局缓存
    private boolean matchCustomRule(TransferRuleItemDTO ruleItem, String appId, String userId, String message) {
        final String type = ruleItem.getType();
        switch (type) {
            case "keywords":
                return matchKeyWord(ruleItem, message);
            default:
                return false;
        }
    }

    private boolean matchUserLabel(TransferRuleItemDTO ruleItem, String appId, String userId) {
        Set<String> userLabelIds = null;
        final Map<String, String> condition = ruleItem.getCondition();
        final String userLabelIdsStr = condition.get("userLabelIds");
        return StrUtil.split(userLabelIdsStr, ",").stream()
                .allMatch(e -> userLabelIds.contains(e));
    }

    private boolean matchTime(TransferRuleItemDTO ruleItem, LocalTime time) {
        final Map<String, String> condition = ruleItem.getCondition();
        // allDay workDay nonWorkDay
        final String dateTypeStr = condition.getOrDefault("dateType", "allDay");
        if (!StrUtil.equals("allDay", dateTypeStr)) {
            final boolean ifWorkDay = true;
            if (StrUtil.equals("workDay", dateTypeStr) && !ifWorkDay) {
                return false;
            } else if (StrUtil.equals("nonWorkDay", dateTypeStr) && ifWorkDay) {
                return false;
            }
        }

        final String startTimeStr = condition.get("startTime");
        if (StrUtil.isNotBlank(startTimeStr)) {
            // 转localtime
            final LocalTime startTime = LocalDateTimeUtil.stringToLocalTime(startTimeStr, "HH:mm");
            if (startTime.compareTo(time) > 0) {
                return false;
            }
        }
        final String endTimeStr = condition.get("endTime");
        if (StrUtil.isNotBlank(endTimeStr)) {
            final LocalTime endTime = LocalDateTimeUtil.stringToLocalTime(endTimeStr, "HH:mm");
            if (endTime.compareTo(time) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean matchKeyWord(TransferRuleItemDTO ruleItem, String inputText) {
        final String matchingRule = ruleItem.getMatchingRule();
        final Map<String, String> condition = ruleItem.getCondition();
        if (StrUtil.equals("preciseMatch", matchingRule)) {
            return StrUtil.equals(condition.get("value"), inputText);
        } else {
            return condition.get("value").contains(inputText);
        }
    }

    @Resource
    private SessionService sessionService;
    @Autowired(required = false)
    private KsRedisCommands ksRedisCommands;

    @Override
    public SessionEntity getLastActiveSession(String appId, String userId) {
        return sessionService.getActiveSession(appId, userId);
    }

    @Override
    public SessionEntity createSession(SessionEntity session) {
        final SessionEntity newSession = createSessionLock(session);
        if (null == newSession) {
            log.error("createSession return,create fail,session:{}", JSONUtil.toJsonStr(session));
            return null;
        }
        doAfterSessionCreate(newSession);
        return newSession;
    }

    // session创建后预留的扩展接口
    private void doAfterSessionCreate(SessionEntity session) {
        // 同步会话，发事件来做
//        cacheSession();
//        synToAdmin();
        // 缓存会话信息
    }

    // 加锁创建session底层服务
    private SessionEntity createSessionLock(SessionEntity session) {
        final String appId = session.getAppId();
        // 客服发给app还是用户发给app
        final String userId = session.getUserId();

        // 加锁：需要和升级会话加锁的key一致
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionLockKey, appId
                , userId);

        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("createSessionLock return,lock fail,event:{}", JSONUtil.toJsonStr(session));
            return null;
        }
        try {
            // 加锁再去查一遍db，防止并发
            final SessionEntity activeSession = this.getLastActiveSession(appId, userId);
            if (null != activeSession) {
                return activeSession;
            }
            final Boolean ret = sessionService.createSession(session);
            if (BooleanUtil.isFalse(ret)) {
                log.error("createSessionLock return,createSession fail,session:{}", JSONUtil.toJsonStr(session));
                return null;
            }
        } catch (Exception e) {
            log.error("createNewSession lock exception, session:{},", JSONUtil.toJsonStr(session), e);
            return null;
        } finally {
            JedisUtils.releaseLockSafe(ksRedisCommands, lockKey, requestId);
        }
        return session;
    }

    @Override
    public void endSession(SessionEntity session) {
        sessionService.endSession(session);
        // 结束掉会话
        doAfterSessionEnd(session);
    }

    // session结束后预留的扩展接口
    private void doAfterSessionEnd(SessionEntity session) {
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

    // 会话转接后预留的扩展接口
    private void doAfterSessionForward(SessionEntity session) {
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

    private boolean validQueueLimit(AssistantGroupInfo groupInfo, Integer queueNum) {
        // 判断排队多少人，有没有设置排队过多不接单
        if (BooleanUtil.isTrue(groupInfo.getIfRejectManyQueue())) {
            if (queueNum >= groupInfo.getRejectQueueNum()) {
                // 发送超过排队上限留言消息，此时还是机器人
                log.info("distributeManualSession return,发送超过排队上限留言消息,queueNum:{}", queueNum);
                return true;
            }
        }
        return false;
    }

    // 排队拦截
    private boolean interceptConfirmQueueSession(AssistantGroupInfo groupInfo, Integer queueNum) {

        // 排队确认拦截
        if (BooleanUtil.isTrue(groupInfo.getIfEnableConfirm())) {
            if (queueNum >= groupInfo.getConfirmNum()) {
                // 有时也需要触发排队确认转人工操作，此时点击回调tryDistributeManualSession
                log.info("distributeManualSession return,排队确认转人工操作,queueNum:{}", queueNum);
                return true;
            }
        }
        return false;
    }

    // 升级操作了，理论上已经算是人工了
    private Boolean handleUpgradeQueueSession(AssistantGroupInfo groupInfo, Integer queueNum, SessionEntity dbSession) {

        // 本次要操作的对象，不要修改参数对象
        final SessionEntity entity = SessionEntity.builder()
                .sid(dbSession.getSid())
                .sessionState("queue")
                .targetType("groupTag")
                .targetId("")
                .groupId(groupInfo.getGroupId())
                .build();

        final Boolean ret = sessionService.upgradeQueueSession(entity);
        if (BooleanUtil.isFalse(ret)) {
            log.info("handleCreateQueueSession return,create session fail,session:{}", JSONUtil.toJsonStr(entity));
            return false;
        }
        // 排队数+1
        final Integer incr = JedisUtils.incr(ksRedisCommands, SessionLockKey.format(SessionLockKey.AppGroupQueueNum, dbSession.getAppId(),
                groupInfo.getGroupId()));
        // 添加到排队列表中
        ksRedisCommands.lpush(SessionLockKey.format(SessionLockKey.AppGroupQueueList, dbSession.getAppId()
                , groupInfo.getGroupId()), entity.getSid());

        doAfterUpgradeQueueSession(dbSession, groupInfo, queueNum);
        return true;
    }

    public void doAfterUpgradeQueueSession(SessionEntity session, AssistantGroupInfo groupInfo, Integer queueNum) {
        // 发送MQ事件或者异步同步数据
        // 排队通知消息
        if (BooleanUtil.isTrue(groupInfo.getIfNoticeManyQueue())) {
            if (queueNum > groupInfo.getNoticeNum()) {
                // 可以排队，发送排队安抚消息，排在第n位，请耐心等待
            }
        }
        synToAdmin();
    }

    private Boolean handleUpgradeManualSession(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo,
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
                return false;
            }
        } catch (Exception e) {
            incrSessionNum(assistantInfo.getAppId(), assistantInfo.getAssistantId());
            log.error("handleUpgradeManualSession exception,session:{},", JSONUtil.toJsonStr(session), e);
        }

        doAfterUpgradeManualSession(session);
        return true;
    }

    public void doAfterUpgradeManualSession(SessionEntity session) {
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
            String sessionFrom, MessageEvent event) {
        if (BooleanUtil.isFalse(groupInfo.getWorking())) {
            // 发送客服组不在工作时间留言消息
            log.info("tryDistributeManualSession return,发送客服组不在工作时间留言消息,session:{}", JSONUtil.toJsonStr(session));
            final String messageKey = messageService.sendCardMessage("groupNotWorkingLeaveMessage", null);
            processRobotMessageToUserEvent(session, messageKey, LocalDateTimeUtil.now());
            return;
        }
        // 查询组内的客服列表
        List<AssistantInfo> assistantList = Lists.newArrayList();
        final List<AssistantInfo> onlineAssistantList = assistantList.stream()
                .filter(e -> StrUtil.equalsAny(e.getState(), "online", "busy"))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(onlineAssistantList)) {
            log.info("tryDistributeManualSession return,无客服在线,发送留言消息,session:{}", JSONUtil.toJsonStr(session));
            final String messageKey = messageService.sendCardMessage("noOnlineAssistantLeaveMessage", null);
            processRobotMessageToUserEvent(session, messageKey, LocalDateTimeUtil.now());
            return;
        }

        // 加锁处理，如果有排队则一定进入排队
        // 如果没有排队，判断是否需要加入到排队中，不排队直接分配客服，分配不到进入排队
        final String lockKey = SessionLockKey.format(SessionLockKey.sessionGroupLockKey, session.getAppId()
                , groupInfo.getGroupId());
        final String requestId = JedisUtils.tryLock(ksRedisCommands, lockKey);
        if (StrUtil.isBlank(requestId)) {
            log.error("tryDistributeManualSession return,加锁失败,session:{}", JSONUtil.toJsonStr(session));
            final String messageKey = messageService.sendNoticeMessage("lockFailNotice", null);
            processRobotMessageToUserEvent(session, messageKey, LocalDateTimeUtil.now());
            return;
        }

        // 再去查一遍db
        final SessionEntity dbSession = sessionService.getSessionBySid(session.getSid());
        try {
            if (null == dbSession) {
                // 会话恰好结束了，忽略掉，因为在会话管理算到这个会话了，会话结束的时候也要加锁
                log.info("tryDistributeManualSession return,会话为空，数据异常,session:{}", JSONUtil.toJsonStr(session));
                final String messageKey = messageService.sendNoticeMessage("sessionNullNotice", null);
                processRobotMessageToUserEvent(session, messageKey, LocalDateTimeUtil.now());
                return;
            }
            if (StrUtil.equals("manual", dbSession.getSessionType())) {
                // 出现并发，已经是人工了
                log.info("tryDistributeManualSession return,并发转发到人工处理,session:{}", JSONUtil.toJsonStr(dbSession));
                // 已经转人工了，直接发送给人工
                processUserMessageToManualEvent(dbSession, event);
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
                // 排队上限拦截
                if (validQueueLimit(groupInfo, queueNum)) {
                    return;
                }
                // 排队二次确认拦截
                if (!StrUtil.equals("userConfirm", sessionFrom) && interceptConfirmQueueSession(groupInfo, queueNum)) {
                    log.info("tryDistributeManualSession return,排队拦截处理,session:{}", JSONUtil.toJsonStr(dbSession));
                    final String messageKey = messageService.sendCardMessage("queueConfirm", null);
                    processRobotMessageToUserEvent(session, messageKey, LocalDateTimeUtil.now());
                    return;
                }
                // 未分配到客服，排队会话
                final Boolean upgradeResult = handleUpgradeQueueSession(groupInfo, queueNum, dbSession);
                // 排队过程中，暂时发给机器人？
                if (BooleanUtil.isTrue(upgradeResult)) {
                    // 发送机器人提示排队消息
                }
            } else {
                // 分配到客服，客服全局会话数和客服本应用会话数已经+1了
                final Boolean upgradeResult = handleUpgradeManualSession(groupInfo, distribute.getAssistantInfo(), dbSession);
                if (BooleanUtil.isTrue(upgradeResult)) {
                    // 发送历史记录和通知
                }
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
    public void processUserMessageToManualEvent(SessionEntity session, MessageEvent event) {
        if (StrUtil.equals("bot", session.getSessionType())) {
            return;
        }

        final String messageKey = event.getInfo().getMessageKey();
        final LocalDateTime messageTime = LocalDateTimeUtil.timestampToLocalDateTime(event.getTimestamp());

        // 用户发给客服的消息
        final String assitantId = session.getAssitantId();
        String forwardMessageKey = messageService.sendMessage(event.getAppId(), null);
        // 消息转发
        if (StrUtil.isBlank(forwardMessageKey)) {
            log.info("processUserMessageToAppEvent return,messageKey is blank,session:{}", JSONUtil.toJsonStr(session));
            return;
        }
        doAfterUserMessageToManual(session, messageKey, messageTime);

    }

    private void doAfterUserMessageToManual(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        // LastMessageId根据时间判断，可以和最后一条消息来判断
        final SessionEntityBuilder builder = SessionEntity.builder()
                .sid(session.getSid())
                .updateTime(LocalDateTime.now())
                .sessionEndMessageId(messageKey)
                .userLastMessageId(messageKey)
                .userLastMessageTime(messageTime)
                .userRequestManualNum(1);

        if (BooleanUtil.isTrue(session.getNoRequest())) {
            builder.noRequest(false)
                    .userFistMessageId(messageKey)
                    .userFistMessageTime(messageTime);
        }
        sessionService.userUpdateSession(builder.build());
    }

    private BotConfig getBotConfig(String appId) {
        return BotConfig.builder()
                .botType("agent")
                .build();
    }

    @Override
    public void processUserMessageToRobotEvent(SessionEntity session, MessageEvent event) {
        if (StrUtil.equals("manual", session.getSessionType())) {
            return;
        }
        // 文本解析，机器人只支持文本消息
        String messageType = event.getInfo().getMessageType();
        String userInput = "";
        BotConfig botConfig = getBotConfig(session.getAppId());
        // 是否开启智能助理
        if (botConfig.isAgent()) {

        } else {

        }
        //查询之前聊天的机器人会话继续
        String robotSessionId = "";
        // robotId和人生成一个空的卡片发送
        String endMessageKey = messageService.sendMessage(session.getAppId(), null);
        // 调用机器人不断获取结果并不断刷新，直到结束

        if (StrUtil.isNotBlank(endMessageKey)) {
            doAfterUserMessageToRobot(session, endMessageKey, LocalDateTimeUtil.now());
        }
    }

    private void doAfterUserMessageToRobot(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        // LastMessageId根据时间判断，可以和最后一条消息来判断
        final SessionEntity build = SessionEntity.builder()
                .sid(session.getSid())
                .updateTime(LocalDateTime.now())
                .sessionEndMessageId(messageKey)
                .userLastMessageId(messageKey)
                .userLastMessageTime(messageTime)
                .userRequestRobotNum(1)
                .build();

        sessionService.userUpdateSession(build);
    }

    @Override
    public void processManualMessageToUserEvent(SessionEntity session, MessageEvent event) {
        // 客服的消息发给用户
        final LocalDateTime now = LocalDateTimeUtil.now();
        String messageKey = messageService.sendMessage(session.getAppId(), null);
        if (StrUtil.isBlank(messageKey)) {
            log.info("processManualMessageToUserEvent return,messageKey is blank,session:{}", JSONUtil.toJsonStr(session));
            return;
        }
        doAfterManualMessageToUser(session, messageKey, now);
    }

    @Override
    public void processRobotMessageToUserEvent(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        if (StrUtil.isBlank(messageKey)) {
            return;
        }
        doAfterRobotMessageToUser(session, messageKey, messageTime);
    }

    // 机器人向用户发送消息
    private void doAfterRobotMessageToUser(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        final SessionEntity build = SessionEntity.builder()
                .sid(session.getSid())
                .updateTime(LocalDateTime.now())
                .sessionEndMessageId(messageKey)
                .userLastMessageId(messageKey)
                .userLastMessageTime(messageTime)
                .build();
        sessionService.userUpdateSession(build);
    }

    @Override
    public void processRobotMessageToManualEvent(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        if (StrUtil.isBlank(messageKey)) {
            return;
        }
        doAfterRobotMessageToManual(session, messageKey, messageTime);
    }

    // 机器人向客服发送消息
    private void doAfterRobotMessageToManual(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        final SessionEntity build = SessionEntity.builder()
                .sid(session.getSid())
                .updateTime(LocalDateTime.now())
                .sessionEndMessageId(messageKey)
                .userLastMessageId(messageKey)
                .userLastMessageTime(messageTime)
                .build();
        sessionService.userUpdateSession(build);
    }

    public void doAfterManualMessageToUser(SessionEntity session, String messageKey, LocalDateTime messageTime) {
        // 记录人工的最后一条消息
        final Boolean noReply = session.getNoReply();
        final SessionEntityBuilder sessionBuilder = SessionEntity.builder()
                .sid(session.getSid())
                .manulLastMessageId(messageKey)
                .manulLastMessageTime(messageTime)
                .updateTime(messageTime)
                .sessionEndMessageId(messageKey)
                .manulReplyNum(1);
        if (BooleanUtil.isTrue(noReply)) {
            // 第一次回复
            sessionBuilder.noReply(false)
                    .manulFirstMessageId(messageKey)
                    .manulFirstMessageTime(messageTime);
        }
        sessionService.manualUpdateSession(sessionBuilder.build());
    }
}
