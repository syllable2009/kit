package com.jxp.flows.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-26 15:03
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum NodeTypeEnum {

    start("start", "开始"),
    end("end", "结束"),
    ifelse("ifelse", "条件语句"),
    cycle("cycle", "循环"),
    codeExec("codeExec", "代码执行"),
    setVariable("setVariable", "设置变量"),
    http("http", "http请求"),
    largeModel("largeModel", "大模型"),
    conditonFlow("conditonFlow", "条件流程编排"),
    parallelFlow("parallelFlow", "并行流程编排"),
    sequentialFlow("sequentialFlow", "顺序流程编排"),
    ;

    private String code;
    private String remark;

    public static final Map<String, NodeTypeEnum> CODE_MAPS =
            Arrays.stream(NodeTypeEnum.values())
                    .collect(Collectors
                            .toMap(NodeTypeEnum::getCode, Function.identity()));

    public static NodeTypeEnum of(Integer code) {
        return Optional.ofNullable(CODE_MAPS.get(code)).orElse(null);
    }
}
