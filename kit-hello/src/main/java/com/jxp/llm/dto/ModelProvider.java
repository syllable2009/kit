package com.jxp.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 14:14
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModelProvider {

    private Long providerId;
    private Long provider;
    private Long teamId;
    private String model;
    private ModelProviderConfig config;
    private boolean ifPrivate;
    private Long tpmLimit;
    private Long rpmLimit;
    private Long rpdLimit;
    private Long updateTime;
}
