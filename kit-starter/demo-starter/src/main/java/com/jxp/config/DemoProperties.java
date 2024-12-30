package com.jxp.config;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 15:22
 */
//@EnableConfigurationProperties
//@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
