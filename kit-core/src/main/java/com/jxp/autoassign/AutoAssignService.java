package com.jxp.autoassign;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.google.common.collect.Maps;
import com.jxp.autoassign.assignstrategy.AssignStrategy;

/**
 * 并发与速度结合
 * @author jiaxiaopeng
 * Created on 2025-01-06 14:59
 */
public class AutoAssignService {

    @Resource
    Map<String, AssignStrategy> assignStrategyMap = Maps.newHashMap();

    // 问题属于哪个appId和技能队列，如何判断技能队列，一个应用一个锁
    public Customer assign(Issue issue, List<String> skills, String appId) {
        // 通过appId获取app配置缓存数据
        // 对此时的客服加锁？
        // 获取技能队列的客服列表List<Customer> customers;
        // 按照分配策略优先级分配，确定了在List<Customer>找到一个Customer的策略
        return null;
    }
}
