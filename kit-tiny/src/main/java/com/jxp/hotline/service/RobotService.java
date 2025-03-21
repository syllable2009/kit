package com.jxp.hotline.service;

import java.util.List;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.domain.entity.SessionEntity;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:57
 */
public interface RobotService {

    void processUserMessage(SessionEntity session, MessageEvent event);

    void sendUserChooseGroupMessage(SessionEntity session, MessageEvent event, List<AssistantGroupInfo> assistantGroups);
}
