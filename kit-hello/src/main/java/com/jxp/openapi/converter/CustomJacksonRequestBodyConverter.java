package com.jxp.openapi.converter;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-17 11:27
 */
public final class CustomJacksonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public RequestBody convert(T value) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(value);
        return RequestBody.create(MEDIA_TYPE, bytes);
    }
}

