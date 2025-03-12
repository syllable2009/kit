package com.jxp.component.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.jxp.component.flow.dto.node.Condition;
import com.jxp.component.flow.dto.node.WeekDayWorkNode;
import com.jxp.component.flow.dto.node.WertherWorkNode;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-03-07 10:42
 */
@Slf4j
public class FlowMain {
    public static void main(String[] args) {
        Map<String, Object> processParams = new HashMap<>();
        final WeekDayWorkNode weekDayWorkNode = new WeekDayWorkNode();
        final WertherWorkNode wertherWorkNode = new WertherWorkNode();
        final Boolean execute1 = weekDayWorkNode.execute(processParams);
        final String execute2 = wertherWorkNode.execute(processParams);
        log.info("execute1:{},execute2:{}", execute1, execute2);

        // 如何有序的构造出流程呢
    }

    private static void test2() {
        List<Condition> conditionList = Lists.newArrayList();
    }
}
