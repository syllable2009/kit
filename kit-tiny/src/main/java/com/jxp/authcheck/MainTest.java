package com.jxp.authcheck;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-02 10:44
 */
@Slf4j
public class MainTest {


    private static String ADMIN_SALT =
            "2cd7a77a9cc74265b46a827ebcad5d4333d41e041ac64a6fb99b3d9a047fdb052c310207f2794cd7a2f3f6fa2c7f9c150539318056d14993b111caf46c580b92";

    public static void main(String[] args) {
//        generateSalt();
        String operator = "wb_zhangminghui";
        final String md5Sign = getMd5Sign(operator, ADMIN_SALT, DateUtil.format(new DateTime(), "yyyy-MM"));
        log.info("sign:{}", md5Sign);
    }


    private static String generateSalt() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            sb.append(IdUtil.fastSimpleUUID());
        }
        return sb.toString();
    }

    private static String getMd5Sign(String... params) {
        String collect = Stream.of(params)
                .collect(Collectors.joining("|"));
        return SecureUtil.md5(collect);
    }

}
