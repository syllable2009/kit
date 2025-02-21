package com.jxp.component.customer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转人工规则
 * @author jiaxiaopeng
 * Created on 2025-02-19 14:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransferManualItemRule {

    // 一条规则包含多个detail，detail之间是且的关系，多个item之间是或的关系
    private List<TransferManualDetailRule> detailRules;

    // 0-全部组 1-指定组
    private int groupType;

    //  指定组时有效
    private List<String> groupIds;

    private Long aid;

    private String uid;

    private String pid;

    private String appId;

    private int weight;

    private int state;

    // 客服分配规则  workload=工作量 饱和度 最久未分配
    private String distributeType;
}
