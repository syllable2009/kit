package com.jxp.tool;

import java.net.ProxySelector;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;


public class HttpProxy {

    private static final String BIZ_CONF_GROUP_TO_HOSTS_KEY = "biz.httpProxyGroupToHosts";
    private static final String COMMON_CONF_GROUP_TO_PROXIES_KEY = "public.httpProxy.CommonProxiesList";

    public static final ProxySelector PROXY_SELECTOR = ProxySelector.getDefault();

    public static final OkHttpClient getDefaultProxiedOkHttpClient() {
        if (EnvUtils.isLocal()) {
            return new OkHttpClient();
        }
        return new OkHttpClient.Builder().proxySelector(PROXY_SELECTOR).build();
    }

    public static final Builder getOkHttpClientBuilderWithProxy() {
        if (EnvUtils.isLocal()) {
            return new OkHttpClient.Builder();
        }
        return new OkHttpClient.Builder().proxySelector(PROXY_SELECTOR);
    }
}
