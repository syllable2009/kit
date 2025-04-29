package com.jxp.easyes;

import org.springframework.context.annotation.Configuration;

import cn.easyes.starter.register.EsMapperScan;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-29 17:46
 */
@Configuration
@EsMapperScan("com.jxp.easyes.mapper")
public class EasyEsConfig {
}
