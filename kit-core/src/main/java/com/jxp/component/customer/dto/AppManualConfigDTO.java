package com.jxp.component.customer.dto;

import java.util.List;

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
public class AppManualConfigDTO {

    private String appId;

    // 转人工关键字，如果是全局的话，此时才有效
    private List<String> keyword;

    // 是否进行人工会话拦截
    private boolean ifManualBlock;

    // 人工拦截提示
    private String manualBlockContent;

    // 是否开启技能队列 0-全局拦截，1-技能队列
    private int blockState;
}
