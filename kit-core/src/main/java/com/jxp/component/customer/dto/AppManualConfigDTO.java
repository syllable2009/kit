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

    private String appName;

    private int appState;

    private List<String> admin;

    // 转人工规则，0-自定义转人工规则 1- 2- 3-强制机器人 4-强制转人工
    private int manualType;

    // 转人工关键字，如果是全局的话，此时才有效
    private List<String> keyword;

    // 是否进行人工会话拦截：0-初始状态，1-人工拦截
    private int ifManualBlock;

    // 人工拦截提示
    private String manualBlockContent;

    // 0-匿名用户 1-登录用户
    private int userType;
}
