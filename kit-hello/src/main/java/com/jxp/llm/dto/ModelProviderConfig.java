package com.jxp.llm.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-12 14:16
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModelProviderConfig {
    private String deploys;

    private String apiBase;

    private String apiKey;

    private String baseUrl;

    private String modelName;

    private String serviceName;

    private String modelSeries;

    private String provider;

    private Map<String, String> customHeaders;

    private Boolean safety;
}
