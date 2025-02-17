package com.jxp.openapi;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.scalars.ScalarsConverterFactory;
/**
 * @author jiaxiaopeng
 * Created on 2025-02-14 17:32
 */
public class OpenApiClientConfig {



    /**
     * 创建 OkHttpClient 客户端
     */
    public static <T> T createOpenApiClient(HttpClientConfigDto httpClientConfig, ConfigDto config,
            Class<T> clientClass) {
        String openApiUrl = httpClientConfig.getUrl();
        //鉴权的url要求必须不能以"/"结尾
        if (openApiUrl.endsWith("/")) {
            openApiUrl = openApiUrl.substring(0, openApiUrl.length() - 1);
        }
        // 定义一个拦截器注入参数
//        OpenApiOAuth openApiOAuth = new OpenApiOAuth(openApiUrl, config.getAppKey(), config.getSecretKey());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(openApiOAuth)
                .connectTimeout(httpClientConfig.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(httpClientConfig.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(httpClientConfig.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(httpClientConfig.getMaxIdleConnections(),
                        httpClientConfig.getKeepAliveTimeSeconds(), TimeUnit.SECONDS))
                .build();
        //baseUrl要求必须以"/"结尾
        if (!openApiUrl.endsWith("/")) {
            openApiUrl = openApiUrl + "/";
        }
        return new Builder()
                .baseUrl(openApiUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create()) //转换基础类型以及 String
                .addConverterFactory(new CustomJacksonConverterFactory()) //转换 Json 对象
                .build()
                .create(clientClass);
    }
}
