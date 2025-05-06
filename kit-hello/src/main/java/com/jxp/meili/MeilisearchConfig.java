package com.jxp.meili;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-06 16:27
 */
@Configuration
public class MeilisearchConfig {

    @Value("${meilisearch.host}")
    private String MEILISEARCH_HOST;

    @Bean
    public Client searchClient() {
        return new Client(new Config(MEILISEARCH_HOST, null));
    }
}
