package com.jxp.service;

import com.jxp.config.DemoProperties;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 15:19
 */
public class DemoService {

    private DemoProperties demoProperties;

    public DemoService(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
    }

    public String sayHello() {
        return "Hello 码农！" + demoProperties.getName();
    }
}
