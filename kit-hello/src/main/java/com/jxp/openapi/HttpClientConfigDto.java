package com.jxp.openapi;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-14 17:36
 */
@Data
public class HttpClientConfigDto {
    /**
     * 域名url（注意只能是域名，不能域名后再带着额外的path）
     */
    private String url;
    /**
     * 连接超时时间（单位：毫秒）
     */
    private long connectTimeoutMillis = 10000;
    /**
     * 读超时时间（单位：毫秒）
     */
    private long readTimeoutMillis = 10000;
    /**
     * 写超时时间（单位：毫秒）
     */
    private long writeTimeoutMillis = 10000;

    /**
     * 每个ip地址的最大空闲连接
     */
    private int maxIdleConnections = 5;

    /**
     * 空闲连接保持时间（单位：秒）
     * 超过此时间，空闲连接将被关闭
     */
    private long keepAliveTimeSeconds = 5 * 60;

    public HttpClientConfigDto(
            @JsonProperty("url") String url,
            @JsonProperty("connectTimeoutMillis") long connectTimeoutMillis,
            @JsonProperty("readTimeoutMillis") long readTimeoutMillis,
            @JsonProperty("writeTimeoutMillis") long writeTimeoutMillis,
            @JsonProperty("maxIdleConnections") int maxIdleConnections,
            @JsonProperty("keepAliveTimeSeconds") long keepAliveTimeSeconds) {
        Preconditions.checkArgument(StringUtils.isNotBlank(url));
        this.url = url;
        if (connectTimeoutMillis > 0) {
            this.connectTimeoutMillis = connectTimeoutMillis;
        }
        if (readTimeoutMillis > 0) {
            this.readTimeoutMillis = readTimeoutMillis;
        }
        if (writeTimeoutMillis > 0) {
            this.writeTimeoutMillis = writeTimeoutMillis;
        }
        if (maxIdleConnections > 0) {
            this.maxIdleConnections = maxIdleConnections;
        }
        if (keepAliveTimeSeconds > 0) {
            this.keepAliveTimeSeconds = keepAliveTimeSeconds;
        }
    }

    public static HttpClientConfigDto getDefault(String url) {
        return new HttpClientConfigDto(url, 0, 0, 0, 0, 0);
    }
}
