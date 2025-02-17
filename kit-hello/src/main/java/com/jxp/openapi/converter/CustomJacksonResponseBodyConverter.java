package com.jxp.openapi.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-17 11:27
 */
public final class CustomJacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Type type;

    public CustomJacksonResponseBodyConverter(Type type) {
        this.type = type;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructType(type);
            return MAPPER.readValue(value.charStream(), javaType);
        } finally {
            value.close();
        }
    }
}
