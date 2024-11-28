package com.jxp.exception;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 15:37
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "error-code-extension", name = "enable", havingValue = "true")
public class ExceptionCodeAutoConfigration implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final String[] beanNamesForType = applicationContext.getBeanNamesForType(IExceptionCode.class);
        Arrays.stream(beanNamesForType)
                .map(applicationContext::getBean)
                .filter(bean -> bean instanceof IExceptionCode)
                .forEach(e -> {
                    Arrays.stream(e.getClass().getFields())
                            .filter(f -> f.isAnnotationPresent(IExceptionCode.class))
                            .forEach(f -> {
                                IExceptionCode a = f.getAnnotation(IExceptionCode.class);
                                ErrorCode.CODE_MAP.put(a.code(), new ErrorCode(a.code(), a.zhCN(), a.enUS()));
                            });
                });
    }
}
