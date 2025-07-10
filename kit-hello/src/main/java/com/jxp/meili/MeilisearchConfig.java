package com.jxp.meili;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.json.JacksonJsonHandler;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-06 16:27
 */
@Configuration
public class MeilisearchConfig {

    @SuppressWarnings("checkstyle:MemberName")
    @Value("${meilisearch.host}")
    private String MEILISEARCH_HOST;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public JacksonJsonHandler jsonHandler(ObjectMapper objectMapper) {
        return new JacksonJsonHandler();
    }

    @Bean
    public Client searchClient(JacksonJsonHandler jsonHandler) {
        return new Client(new Config(MEILISEARCH_HOST, "R5T5WDon_QrPqhFK97NgGlTVa81iuVlN44TMLiClTTg", jsonHandler));
    }


}
