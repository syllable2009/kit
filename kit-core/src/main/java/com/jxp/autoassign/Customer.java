package com.jxp.autoassign;

import java.util.List;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-01-06 14:54
 */

@Data
public class Customer {

    private Long aid;

    private String uid;

    private String name;

    // 技能队列=组
    private List<String> skills;

    // 单个应用分配上限
    private int maxServiceCount;

    // 全局分配上限，因为一个客服可以属于多个应用
    private int maxGlobalCount;

    // 配置策略的参数都得缓存
}
