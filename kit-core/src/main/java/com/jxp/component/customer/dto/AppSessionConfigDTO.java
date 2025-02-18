package com.jxp.component.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 17:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AppSessionConfigDTO {

    private String appId;

    // 会话间隔时间，单位秒
    private Long sessionInterval;

    // 工作时间开始
    private String workStartTime;
    // 工作时间结束
    private String workEndTime;

}
