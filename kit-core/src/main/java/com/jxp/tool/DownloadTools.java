package com.jxp.tool;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

@Slf4j
public class DownloadTools {

    public static byte[] download(String fileUrl) throws IOException {
        OkHttpClient okHttpClient = HttpProxy.getDefaultProxiedOkHttpClient();
        Request request = new Builder().url(fileUrl).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("download file failed. " + response);
            }
            return response.body().bytes();
        }
    }

    public static Response download2(String fileUrl) throws IOException {
        OkHttpClient okHttpClient = HttpProxy.getDefaultProxiedOkHttpClient();
        Request request = new Builder().url(fileUrl).build();
        return okHttpClient.newCall(request).execute();
    }
}
