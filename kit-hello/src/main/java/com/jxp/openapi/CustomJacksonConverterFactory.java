package com.jxp.openapi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.jxp.openapi.converter.CustomJacksonRequestBodyConverter;
import com.jxp.openapi.converter.CustomJacksonResponseBodyConverter;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-14 17:57
 */
public final class CustomJacksonConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
            Retrofit retrofit) {
        return new CustomJacksonResponseBodyConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
            Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new CustomJacksonRequestBodyConverter<>();
    }
}

