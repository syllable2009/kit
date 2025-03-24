package com.jxp.hotline.service.impl;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.AssistantInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.service.SessionService;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:33
 */

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Override
    public SessionEntity getActiveSession(String messageServerId, String userId) {
        return null;
    }

    @Override
    public SessionEntity getSessionBySid(String sessionId) {
        return null;
    }

    @Override
    public Boolean createSession(SessionEntity sessionEntity) {
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

    // 客服自动分配，只分配一个客服组
    private void doGroupDistributeAssistant(AssistantGroupInfo groupInfo, AssistantInfo assistantInfo) {

    }

}
