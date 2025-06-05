package com.jxp.flows.service;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;
import com.jxp.flows.service.flow.ConditionWorkFlow;
import com.jxp.flows.service.flow.ParallelWorkFlow;
import com.jxp.flows.service.flow.SequentialWorkFlow;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-05 11:39
 */
public class FlowUtils {


    public static boolean execNode(INode node, FlowContext context) {
        final NodeTypeEnum nodeType = node.getNodeType();
        switch (nodeType) {
            case conditonFlow:
                final ConditionWorkFlow conditionWorkFlow = (ConditionWorkFlow) node;
                return conditionWorkFlow.execute(context);
            case parallelFlow:
                final ParallelWorkFlow parallelWorkFlow = (ParallelWorkFlow) node;
                return parallelWorkFlow.execute(context);
            case sequentialFlow:
                final SequentialWorkFlow sequentialWorkFlow = (SequentialWorkFlow) node;
                return sequentialWorkFlow.execute(context);
            default:
                return node.execute(context);
        }
    }

    public static boolean validInputParam(INode node, FlowContext context) {
        // 节点的需求参数
        final List<Param> input = node.getInput();
        input.stream()
                .filter(e -> BooleanUtils.isTrue(e.getRequired()))
                .forEach(e -> {
                    final String category = e.getCategory();
                });
        return true;
    }
}
