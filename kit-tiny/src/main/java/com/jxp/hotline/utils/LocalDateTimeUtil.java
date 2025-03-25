package com.jxp.hotline.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

    // 将时间字符串按指定格式转换为 LocalTime  "09:15" "HH:mm"
    public static LocalTime stringToLocalTime(String timeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeStr, formatter);
    }
}
