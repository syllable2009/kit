package com.jxp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.jxp.service.DemoService;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-30 15:18
 */

//@AutoConfigureAfter({DemoAutoConfig.class})
@EnableConfigurationProperties({DemoProperties.class})
@Configuration
@PropertySource(name = "CoderAcademy Default Properties", value = "classpath:/META-INF/demo" +
        "-default.properties")
//@ConditionalOnMissingBean(DemoService.class)
@ConditionalOnProperty(prefix = "demo.starter", name = "enable", havingValue = "true")
public class DemoAutoConfig {

    @Bean
    public DemoService demoService(DemoProperties demoProperties) {
        return new DemoService(demoProperties);
    }
}
