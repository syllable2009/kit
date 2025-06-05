package com.jxp.flows.service.flow;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;
import com.jxp.flows.service.AbstractNodeFlow;
import com.jxp.flows.service.FlowUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class SequentialWorkFlow extends AbstractNodeFlow {

    // node或者workflow
    private List<INode> nodes;


    public static SequentialWorkFlow builder() {
        final SequentialWorkFlow workFlow = new SequentialWorkFlow();
        workFlow.setNodes(Lists.newArrayList());
        return workFlow;
    }

    public SequentialWorkFlow build() {
        if (null == this.getInput()) {
            this.setInput(Collections.EMPTY_LIST);
        }
        return this;
    }

    public SequentialWorkFlow then(INode node) {
        this.nodes.add(node);
        return this;
    }

    public SequentialWorkFlow then(List<INode> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

    @Override
    public String getNodeId() {
        return "SequentialWorkFlow";
    }

    @Override
    public boolean execute(FlowContext flowContext) {
        final List<INode> nodes = this.getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            this.setNodeResult(NodeResult.fail("no node exec"));
            return false;
        }
        // 顺序执行
        for (INode node : nodes) {
            final boolean execute = FlowUtils.execNode(node, flowContext);
            if (execute) {
                node.setNodeResult(NodeResult.success());
                flowContext.putExecuteNode(node.getNodeId(), node);
            } else {
                node.setNodeResult(NodeResult.fail("node result is null"));
                return false;
            }
        }
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.sequentialFlow;
    }
}
