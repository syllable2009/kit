package com.jxp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 15:22
 */
@Component
@ConfigurationProperties(prefix = "demo.starter")
public class DemoProperties {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
