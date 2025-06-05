package com.jxp.flows.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-26 14:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Param {
    // 变量类型:userInput用户输入,node节点previousNodeVariable，系统变量systemVariable，自定义变量customVariable
    private String category;
    // 变量名称
    private String name;
    // 变量值
    private String value;
    // 值的类型
    private String type;
    // 格式化
    private String format;
    // 描述
    private String remark;
    // 是否必须
    private Boolean required;
}
