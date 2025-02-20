package com.jxp.component.customer.dto;

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
public class TransferManualDetailRule {

    private Long aid;

    private String uid;

    private String pid;

    private String appId;

    private int state;

    // keyword=关键词 员工性质  工作性质 当前时间 员工标签等
    private String ruleType;

    // 精准匹配 模糊匹配 等于 不等于 属于 包含等
    private String conditionKey;

    // 具体情况的json值，转成map对象
    private String conditionValue;

}
