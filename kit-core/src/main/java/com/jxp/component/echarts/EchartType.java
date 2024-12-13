package com.jxp.component.echarts;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型
 */
@NotNull
@AllArgsConstructor
@Getter
public enum EchartType {

    Line("line", "线图"),
    Bar("bar", "柱形图"),
    Pie("pie", "饼图"),
    Scatter("scatter", "散点图"),
    ;

    private static final Map<String, EchartType> VALUE_MAP =
            Arrays.stream(EchartType.values())
                    .collect(Collectors.toMap(EchartType::getCode, Function.identity()));

    private String code;
    private String name;

    public static EchartType of(String type) {
        return VALUE_MAP.get(type);
    }
}
