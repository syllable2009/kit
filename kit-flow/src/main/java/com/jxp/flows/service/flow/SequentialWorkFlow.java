package com.jxp.flows.service.flow;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;
import com.jxp.flows.service.AbstractNodeFlow;

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

    public INode add(INode node) {
        this.nodes.add(node);
        return this;
    }

    public INode add(List<INode> nodes) {
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
            this.setNodeResult(NodeResult.fail(flowContext, "no node exec"));
            return false;
        }
        // 顺序执行
        for (INode node : nodes) {
            final boolean execute = node.execute(flowContext);
            if (execute) {
                node.setNodeResult(NodeResult.fail(flowContext, "node result is null"));
                return true;
            } else {
                node.setNodeResult(NodeResult.success(flowContext, null));
                flowContext.putExecuteNode(node.getNodeId(), node);
            }
        }
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.flow;
    }
}
