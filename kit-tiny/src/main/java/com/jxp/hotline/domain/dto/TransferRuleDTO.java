package com.jxp.hotline.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-25 16:18
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferRuleDTO {

    private String appId;
    private String ruleName;
    private String ruleDesc;
    // 每个List<TransferRuleItemDTO>是且的关系
    // List<List<TransferRuleItemDTO>>是或的关系
    private List<List<TransferRuleItemDTO>> triggerConditionList;
    // 所有组和自定义组 allGroup customerGroup
    private String customerGroupType;
    // 自定义的列表组
    private List<CustomerGroupDTO> customerGroupList;
}
