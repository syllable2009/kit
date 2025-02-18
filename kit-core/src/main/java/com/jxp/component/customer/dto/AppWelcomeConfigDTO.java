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
public class AppWelcomeConfigDTO {

    private String appId;

    // 打招呼间隔时间，单位秒
    private Long heloInterval;

    private String heloContent;

}
