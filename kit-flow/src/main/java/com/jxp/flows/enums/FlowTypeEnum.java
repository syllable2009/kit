package com.jxp.flows.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-26 15:03
 */
@Deprecated
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FlowTypeEnum {

    node("node", "节点"),
    agent("agent", "agent"),
    plugin("plugin", "插件"),
    workflow("workflow", "工作流"),
    action("action", "执行动作"),
    ;

    private String type;
    private String remark;

}
