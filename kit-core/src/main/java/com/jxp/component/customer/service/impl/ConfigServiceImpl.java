package com.jxp.component.customer.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jxp.component.customer.dto.AppLeaveMessageConfigDTO;
import com.jxp.component.customer.dto.AppManualConfigDTO;
import com.jxp.component.customer.dto.AppSessionConfigDTO;
import com.jxp.component.customer.dto.AppWelcomeConfigDTO;
import com.jxp.component.customer.dto.ManualGroupConfigDTO;
import com.jxp.component.customer.dto.TransferManualItemRule;
import com.jxp.component.customer.dto.TransferManualRule;
import com.jxp.component.customer.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-21 16:38
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public AppSessionConfigDTO getAppSessionConfig(String appId) {
        return null;
    }

    @Override
    public AppManualConfigDTO getManualConfigDTO(String appId) {
        return null;
    }

    @Override
    public AppWelcomeConfigDTO getAppWelcomeConfigDTO(String appId) {
        return null;
    }

    @Override
    public AppLeaveMessageConfigDTO getLeaveMessageConfig(String appId) {
        return null;
    }

    @Override
    public ManualGroupConfigDTO getManualGroupConfig(String appId) {
        return null;
    }

    @Override
    public TransferManualRule getTransferManualRule(String appId) {
        return null;
    }

    @Override
    public List<TransferManualItemRule> getTransferManualItemRule(String appId) {
        return null;
    }
}
