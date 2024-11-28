package com.jxp.setting.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 可以做成一张表
 * @author jiaxiaopeng
 * Created on 2024-10-30 16:31
 */
@AllArgsConstructor
@Getter
public enum SetBizIdEnum {
    ENCY("ency", "百科"),
    RECOMMEND("recommend", "精选"),
    TERM("team", "任务管理"),
    ;

    private String code;
    private String desc;


    private static final Map<String, SetBizIdEnum> CODE_MAP =
            Arrays.stream(SetBizIdEnum.values())
                    .collect(Collectors.toMap(SetBizIdEnum::getCode, Function.identity()));

    public static SetBizIdEnum of(String code) {
        return CODE_MAP.get(code);
    }
}
