package com.jxp.flows.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.enums.ParamCategory;
import com.jxp.flows.infs.INode;
import com.jxp.flows.service.flow.ConditionWorkFlow;
import com.jxp.flows.service.flow.ParallelWorkFlow;
import com.jxp.flows.service.flow.SequentialWorkFlow;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-05 11:39
 */
@Slf4j
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

    // 校验参数,有一个为空立即报错返回，true代表验证通过
    public static boolean validInputParam(INode node, FlowContext context) {
        // 节点的需求参数
        final List<Param> input = node.getInput();
        final boolean present = input.stream()
                .filter(e -> BooleanUtils.isTrue(e.getRequired()))
                .filter(e -> {
                    final Param param = context.getInput().get(e.getName());
                    final boolean b = null == param;
                    if (b) {
                        log.error("validInputParam error, nodeId:{}:{}, param:{} is null", node.getNodeId(),
                                node.getName(), e.getName());
                    }
                    return b;
                })
                .findFirst()
                .isPresent();
        if (present) {
            return false;
        }
        return true;
    }


    public static List<Param> paramConvertList(List<Param> input, FlowContext context) {
        if (CollectionUtils.isEmpty(input)) {
            return Collections.EMPTY_LIST;
        }
        return input.stream()
                .map(e -> {
                            final ParamCategory category = e.getCategory();
                            if (ParamCategory.userInput.equals(category)) {
                                return context.getInput().get(e.getName());
                            } else if (ParamCategory.nodeVariable.equals(category)) {
                                return context.getExecuteMap().get(e.getName()).getResult().getOutput().get(e.getName());
                            } else if (ParamCategory.systemVariable.equals(category)) {
                                return context.getGlobalMap().get(e.getName());
                            } else if (ParamCategory.customVariable.equals(category)) {
                                return context.getGlobalMap().get(e.getName());
                            } else {
                                return null;
                            }
                        }
                ).collect(Collectors.toList());
    }

    public static Map<String, Param> paramConvertMap(List<Param> input, FlowContext context) {
        return paramConvertList(input, context)
                .stream()
                .collect(Collectors.toMap(Param::getName, Function.identity(), (k1, k2) -> k2));
    }
}
