package com.jxp.openapi;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-14 17:40
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import lombok.Getter;

/**
 * OpenApi配置
 * {"appKey":"xxxx", "secretKey":"xxxxx"}
 */
@Getter
public class ConfigDto {

    private static final ConfigDto DEFAULT = new ConfigDto("", "");

    private final String appKey;
    private final String secretKey;

    @JsonCreator
    public ConfigDto(@JsonProperty("appKey") String appKey, @JsonProperty("secretKey") String secretKey) {
        this.appKey = Preconditions.checkNotNull(appKey);
        this.secretKey = Preconditions.checkNotNull(secretKey);
    }
}