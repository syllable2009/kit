package com.jxp.resultcode;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jiaxiaopeng
 * Created on 2024-11-28 16:51
 */


@Getter
@AllArgsConstructor
public enum CommonResultCode {

    OK(0, "成功", "success"),
    FAIL(-1, "失败", "fail"),
    NO_LOGIN(401,"未登录","not login"),
    NO_AUTH(403,"无权限","no permission"),
    ;

    private final Integer code;
    private final String zhCn;
    private final String enUs;

    public static final Map<Integer, CommonResultCode> CODE_MAP =
            Arrays.stream(CommonResultCode.values())
                    .collect(Collectors.toMap(CommonResultCode::getCode, Function.identity()));

    public static CommonResultCode of(String code) {
        return CODE_MAP.get(code);
    }
}
