package com.jxp.component.flow.dto.node;

import java.util.Map;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-07 10:40
 */
public class WertherWorkNode implements WorkNode<String> {
    @Override
    public String execute(Map<String, Object> inputs) {
        return "today is nice";
    }
}
