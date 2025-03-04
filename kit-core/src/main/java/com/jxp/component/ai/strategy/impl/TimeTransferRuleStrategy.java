package com.jxp.component.ai.strategy.impl;

import java.util.Map;

import com.jxp.component.ai.strategy.TransferRuleStrategy;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-04 10:55
 */
public class TimeTransferRuleStrategy implements TransferRuleStrategy {
    @Override
    public boolean applyStrategy(String appId, String userId, String param, Map<String, String> extraMap) {
        return false;
    }
}
