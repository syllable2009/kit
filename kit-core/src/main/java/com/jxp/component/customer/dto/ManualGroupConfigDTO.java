package com.jxp.component.customer.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服组配置
 * @author jiaxiaopeng
 * Created on 2025-02-18 17:10
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ManualGroupConfigDTO {

    private String appId;

    private Map<String, Map<String, String>> manualGroup;

    // 工作时间开始
    private String workStartTime;
    // 工作时间结束
    private String workEndTime;

}
