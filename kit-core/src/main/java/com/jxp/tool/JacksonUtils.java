package com.jxp.tool;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtils {

    private static final String EMPTY_JSON = "{}";
    private static final String EMPTY_ARRAY_JSON = "[]";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .registerModule(new GuavaModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL); //不返回 Null 值;


    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static <T> T toBean(String jsonString, Class<T> beanClass) {
        try {
            return MAPPER.readValue(jsonString, beanClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String str, TypeReference<T> valueTypeRef) {
        try {
            return MAPPER.readValue(str, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map toMap(String str) {
        try {
            return MAPPER.readValue(str, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonStr(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String string, TypeReference<List<T>> typeReference) {
        try {
            return MAPPER.readValue(string, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode parseObj(byte[] content) {
        try {
            return MAPPER.readTree(content);
        } catch (Exception e) {
            log.error("JacksonUtils parseObj error,content:{}", content, e);
        }
        return null;
    }

    public static JsonNode parseObj(String str) {
        try {
            return MAPPER.readTree(str);
        } catch (Exception e) {
            log.error("JacksonUtils parseObj error,str:{}", str, e);
        }
        return null;
    }


}
