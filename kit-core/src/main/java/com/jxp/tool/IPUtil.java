package com.jxp.tool;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2023-06-28 16:38
 */
@Slf4j
public class IPUtil {

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = getIpFromHeader(request);
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (StrUtil.isNotBlank(ip) && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                return ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    private static String getIpFromHeader(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-real-ip");
            if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            } else {
                return ip;
            }
            if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            } else {
                return ip;
            }
            if (StrUtil.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            } else {
                return ip;
            }
            if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            } else {
                return ip;
            }
            if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            } else {
                return ip;
            }
            if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            } else {
                return ip;
            }
            return ip;
        } catch (Exception e) {
            log.error("IPUtil ERROR：", e);
        }
        return ip;
    }
}
