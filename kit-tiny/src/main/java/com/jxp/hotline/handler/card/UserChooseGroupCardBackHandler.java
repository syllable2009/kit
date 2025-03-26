package com.jxp.hotline.handler.card;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jxp.hotline.domain.dto.CardInfo;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.SessionEntity;
import com.jxp.hotline.handler.AbstractCardCallBackHandler;
import com.jxp.hotline.service.SessionManageService;
import com.jxp.hotline.service.SessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-26 15:49
 */
@Component
@Slf4j
public class UserChooseGroupCardBackHandler extends AbstractCardCallBackHandler<String> {

    @Resource
    private SessionManageService robotSessionManageService;
    @Resource
    private SessionService sessionService;

    @Override
    protected void initializeActions() {
        attributeActions.put("submit_button", this::submit);
        attributeActions.put("add_button", this::submit);
        attributeActions.put("del_button", this::submit);
    }

    @Override
    public String getBizId() {
        return "userChooseAssitantGroup";
    }

    @Override
    protected Class<String> getParamType() {
        return String.class;
    }

    private String submit(CardInfo info, Object entity) {
        // 选择的组id
        final String groupId = info.getActionValue().get("grouId");

        final SessionEntity activeSession = sessionService.getActiveSessionByUserId("appId", "userId");
        if (null == activeSession) {
            return null;
        }
        // 获取组的配置
        AssistantGroupInfo assistantGroupInfo = null;
        robotSessionManageService.tryDistributeManualSession(activeSession, assistantGroupInfo, "userChoose", null);
        return null;
    }
}
