package com.jxp.delayevent;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-16 16:41
 */
@Configuration
public class DelayDeventConfig {

    @Bean
    Map<DelayEventType, DelayEventHandle> delayEventProcessorMap() {
        return new HashMap<>();
    }
}
