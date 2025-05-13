package com.jxp.nt.dspringboot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-09 16:29
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    /**
     * boss线程数量
     */
    private Integer boss;

    /**
     * worker线程数量
     */
    private Integer worker;

    /**
     * 连接超时时间
     */
    private Integer timeout = 60000;

    /**
     * 服务器主端口
     */
    private Integer port = 18023;

    /**
     * 服务器备用端口
     */
    private Integer portSalve = 18026;

    /**
     * 服务器地址 默认为本地
     */
    private String host = "127.0.0.1";
}
