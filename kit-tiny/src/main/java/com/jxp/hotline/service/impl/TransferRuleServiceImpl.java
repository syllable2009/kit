package com.jxp.hotline.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jxp.hotline.domain.dto.MessageEvent;
import com.jxp.hotline.domain.entity.AssistantGroupInfo;
import com.jxp.hotline.service.TransferRuleService;

import lombok.extern.slf4j.Slf4j;

/**
 * 按照规则匹配客服组
 * @author jiaxiaopeng
 * Created on 2025-03-20 15:37
 */

@Service
@Slf4j
public class TransferRuleServiceImpl implements TransferRuleService {

    @Override
    public List<AssistantGroupInfo> matchLiveGroup(MessageEvent event) {
        return null;
    }

}
