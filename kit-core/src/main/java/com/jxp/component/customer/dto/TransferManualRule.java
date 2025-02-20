package com.jxp.component.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转人工规则，不区分规则类型
 * @author jiaxiaopeng
 * Created on 2025-02-19 14:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransferManualRule {

    private String appId;

    private Long aid;

    private String uid;

    // 规则名称
    private String ruleName;

    // 规则描述
    private String ruleDesc;

    // 创建时间
    private String createTime;

    // 创建人
    private String createId;

    // 更新时间
    private String updateTime;

    // 更新人
    private String updateId;

    // 状态 0-有效
    private int state;

}
