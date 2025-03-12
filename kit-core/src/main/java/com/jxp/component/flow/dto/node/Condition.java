package com.jxp.component.flow.dto.node;

import java.util.Map;

/**
 * 条件接口
 * @author jiaxiaopeng
 * Created on 2025-03-07 10:13
 */
public class Condition {

    // 条件类型：IF CURRENT_TIME
    private String type;

    // contain equals is not BELONGS
    private String rule;

    // json字符串
    private String condition;

    private String expression; // 例如: "inputs.get('status') == 'APPROVED'"

    public boolean evaluate(Map<String, Object> inputs) {
        // 实现条件解析逻辑
        return true;
    }
}


