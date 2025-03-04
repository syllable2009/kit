package com.jxp.component.ai.strategy;

import java.util.Map;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:54
 */
public interface TransferRuleStrategy {
    boolean applyStrategy(String appId, String userId, String param, Map<String, String> extraMap);
}

