package com.jxp.flows.service.node;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.service.AbstractNode;
import com.jxp.flows.service.FlowUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 设置变量节点
 * @author jiaxiaopeng
 * Created on 2025-06-05 10:23
 */
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class SetVariableNode extends AbstractNode {

    @Override
    public boolean execute(FlowContext context) {
        log.info("设置变量节点");
        final List<Param> input = this.getInput();
        final List<Param> params = FlowUtils.paramConvert(input, context);
        final Map<String, Param> paramMap = params.stream().collect(Collectors.toMap(Param::getName,
                Function.identity(), (k1, k2) -> k2));
        context.getGlobalMap().putAll(paramMap);
        // 保存执行结果
        this.setNodeResult(NodeResult.success(paramMap));
        context.putExecuteNode(this.getNodeId(), this);
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.setVariable;
    }

    @Override
    public String getNodeId() {
        return "SetVariableNode";
    }
}
