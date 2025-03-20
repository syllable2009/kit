package com.jxp.hotline.service;

import java.util.List;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:37
 */
public interface TransferRuleService {
    List<AssistantGroupInfo> matchLiveGroup(MessageEvent event);
}
