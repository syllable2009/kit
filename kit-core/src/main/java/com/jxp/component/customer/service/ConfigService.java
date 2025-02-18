package com.jxp.component.customer.service;

import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-18 16:52
 */
public interface ConfigService {

    AppSessionConfigDTO getAppSessionConfig(String appId);

    AppManualConfigDTO getManualConfigDTO(String appId);
}
