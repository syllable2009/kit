package com.jxp.component.customer.service;

import com.jxp.component.customer.dto.AppLeaveMessageConfigDTO;
import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.AppWelcomeConfigDTO;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:52
 */
public interface ConfigService {

    // 获取app全局配置
    AppSessionConfigDTO getAppSessionConfig(String appId);

    // 获取app人工配置
    AppManualConfigDTO getManualConfigDTO(String appId);

    // 获取app欢迎配置
    AppWelcomeConfigDTO getAppWelcomeConfigDTO(String appId);

    // 获取留言配置
    AppLeaveMessageConfigDTO getLeaveMessageConfig(String appId);

}
