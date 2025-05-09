package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.SessionService;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:33
 */

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Override
    public SessionEntity getActiveSessionByUserId(String appId, String userId) {
//        lambdaQuery()
//                .last(" limit 1")
//                .one();
        return null;
    }

    @Override
    public SessionEntity getActiveSessionByGroupId(String appId, String groupId) {
//        lambdaQuery()
//                .last(" limit 1")
//                .one();
        return null;
    }

    @Override
    public SessionEntity getSessionBySid(String sessionId) {
        return null;
    }

    @Override
    public Boolean createSession(SessionEntity sessionEntity) {
        if (null == sessionEntity.getUserRequest()) {
            sessionEntity.setUserRequest(false);
        }
        if (null == sessionEntity.getManualReply()) {
            sessionEntity.setManualReply(false);
        }
        if (null == sessionEntity.getUserRequestRobotNum()) {
            sessionEntity.setUserRequestRobotNum(0);
        }
        if (null == sessionEntity.getUserRequestManualNum()) {
            sessionEntity.setUserRequestManualNum(0);
        }
        if (null == sessionEntity.getManualReplyNum()) {
            sessionEntity.setManualReplyNum(0);
        }
        return null;
    }

    @Override
    public Boolean upgradeQueueSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public Boolean upgradeManualSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public Boolean distributeSession(SessionEntity sessionEntity) {
        return null;
    }

    @Override
    public void handleManualSessionStartEvent(SessionEntity sessionEntity) {
        // 判断会话类型，如果是机器人返回
    }

    @Override
    public void handleManualSessionEndEvent(SessionEntity sessionEntity) {
        // 处理会话结束落库
        // 判断会话类型
        if (StrUtil.equals("manual", sessionEntity.getSessionType())) {
            // 判断自动分配
            AssistantGroupInfo groupInfo = null;
            if (BooleanUtil.isTrue(groupInfo.getAutoDistribute())) {
                // 判断是否给用户自动分配
                // 查询客服信息
                AssistantInfo assistantInfo = null;
                doGroupDistributeAssistant(groupInfo, assistantInfo);
            }
        }
    }

    @Override
    public Boolean endSession(SessionEntity session) {
        // 设置会话的结束原因和状态
//        lambdaUpdate()
        return true;
    }

    @Override
    public Boolean manualUpdateSession(SessionEntity session) {
        // lambdaUpdate()
        // 客服消息数+1
        log.info("manualUpdateSession,session:{}", JSONUtil.toJsonStr(session));
        return null;
    }

    @Override
    public Boolean robotUpdateSession(SessionEntity session) {
        // 用户消息数+1
        log.info("robotUpdateSession,session:{}", JSONUtil.toJsonStr(session));
        return true;
    }

    // 客服自动分配，只分配一个客服组
    private void doGroupDistributeAssistant(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo) {

    }

}
