package com.jxp.component.ai.strategy;

import java.util.List;
import java.util.Map;

import com.jxp.component.ai.strategy.impl.KeywordTransferRuleStrategy;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-04 10:58
 */
@Slf4j
public class AppRun {

    public static void main(String[] args) {

    }

    // 尝试着进行自定义人工规则匹配，如果成功则进入人工，否则进入机器人知识库
    public void tryMatchTransferRule() {
        // 1.全局的是否开启人工
        boolean enableManualService = true;
        if (BooleanUtil.isTrue(enableManualService)) {
            final boolean matchCustomRuleResult = invokeMatchCustomRule("appId", "userId", "hello");
            if (BooleanUtil.isTrue(matchCustomRuleResult)) {
                return;
            } else {
                log.info("appId={},userId={} not match custom rule");
            }
        } else {
            log.info("appId={},userId={} not enable manual service");
            // 机器人
        }
    }

    // 利用策略模式来代替原来的if-else进行规则匹配
    // 匹配到了就执行
    public boolean invokeMatchCustomRule(String appId, String userId, String message) {
        // 获取转人工规则缓存的json字符数组
        String ruleString = "";
        // 转成list
        final List<Map> list = JSONUtil.toList(ruleString, Map.class);
        final List<Map<String, String>> ruleList = JSONUtil.toBean(ruleString,
                new TypeReference<>() {
                }, true);
        // 先匹配关键字
        final KeywordTransferRuleStrategy keywordTransferRuleStrategy = new KeywordTransferRuleStrategy();

        for (Map<String, String> m : ruleList) {
            if (!keywordTransferRuleStrategy.applyStrategy(appId, userId, message,
                    null)) {
                continue;
            }
            // 通过标识获取其他的规则
            final Map<String, String> matchRuleObj = ruleList.stream()
//                    .filter(e ->  && )
                    .findFirst()
                    .orElse(null);
//                .orElseGet(null);
        }

        return true;
    }
}
