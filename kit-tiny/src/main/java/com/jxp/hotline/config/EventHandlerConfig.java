package com.jxp.hotline.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jxp.hotline.annotation.EventType;
import com.jxp.hotline.handler.EventHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-20 11:10
 */
@Configuration
public class EventHandlerConfig implements ApplicationContextAware {

    private static Map<String, EventHandler> eventHandlerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EventType.class);
        beans.forEach((beanName, bean) -> {
            EventType annotation = bean.getClass().getAnnotation(EventType.class);
            eventHandlerMap.put(annotation.value(), (EventHandler) bean);
        });
    }

    @Bean
    public Map<String, EventHandler> eventHandlerMap() {
        return eventHandlerMap;
    }
}
