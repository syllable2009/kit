package com.jxp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.service.DemoService;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 15:18
 */
@Configuration
public class DemoAutoConfig {

    @Bean
    public DemoService demoService() {
        return new DemoService();
    }
}
