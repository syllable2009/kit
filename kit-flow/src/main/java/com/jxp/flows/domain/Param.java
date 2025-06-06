package com.jxp.flows.domain;

import com.jxp.flows.enums.ParamCategory;
import com.jxp.flows.enums.ParamType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置和时间传参共用对象
 * 实际传参只需要name和value即可
 * @author jiaxiaopeng
 * Created on 2025-05-26 14:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Param {
    // 变量类型:userInput用户输入,node节点nodeVariable，系统变量systemVariable，自定义变量customVariable
    private ParamCategory category;
    // 变量名称
    private String name;
    // 变量值
    private String value;
    // 值的类型
    private ParamType type;
    // 格式化
    private String format;
    // 描述
    private String remark;
    // 是否必须
    private Boolean required;
    // 默认值
    private String defaultValue;
}
