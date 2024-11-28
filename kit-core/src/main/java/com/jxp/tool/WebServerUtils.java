package com.jxp.tool;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 17:28
 */
public final class WebServerUtils {
    public static final int UNKNOWN_PORT = 0;
    private static volatile int port = 0;

    public WebServerUtils() {
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        WebServerUtils.port = port;
    }
}
