package com.jxp.component.flow.dto.node;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-07 10:40
 */
public class IfElseWorkNode implements WorkNode<String> {

    List<Condition> conditionList = Lists.newArrayList();

    @Override
    public String execute(Map<String, Object> inputs) {
        return "if-else";
    }
}
