package com.jxp.hotline.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-21 17:10
 */
public class LocalDateTimeUtil {

    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
}
