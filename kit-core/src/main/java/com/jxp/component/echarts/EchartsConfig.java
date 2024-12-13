package com.jxp.component.echarts;

import org.icepear.echarts.render.Engine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jknack.handlebars.Handlebars;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-13 14:45
 */

@Configuration
public class EchartsConfig {

    @Bean
    public Engine engine() {
        return new Engine();
    }

    @Bean
    public Handlebars handlebars() {
        return new Handlebars();
    }
}
