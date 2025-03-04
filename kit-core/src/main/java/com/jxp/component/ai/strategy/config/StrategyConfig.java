package com.jxp.component.ai.strategy.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-04 11:42
 */
@Configuration
public class StrategyConfig implements ApplicationContextAware {
    private Map<String, Strategy> strategyMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) {
        Map<String, Object> beans = context.getBeansWithAnnotation(HandlerType.class);
        beans.forEach((beanName, bean) -> {
            HandlerType annotation = bean.getClass().getAnnotation(HandlerType.class);
            strategyMap.put(annotation.value(), (Strategy) bean);
        });
    }

    @Bean
    public Map<String, Strategy> strategyMap() {
        return strategyMap;
    }
}
