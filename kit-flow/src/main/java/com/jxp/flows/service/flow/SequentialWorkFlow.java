package com.jxp.flows.service.flow;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.enums.NodeState;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.service.AbstractNodeFlow;
import com.jxp.flows.service.INode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:32
 */

@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class SequentialWorkFlow extends AbstractNodeFlow {

    @Override
    public String getNodeId() {
        return "SequentialWorkFlow";
    }

    @Override
    public NodeResult execute(FlowContext flowContext) {
        final List<INode> nodes = this.getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            return NodeResult.fail(flowContext, "no node exec");
        }
        // 顺序执行
        for (INode node : nodes) {
            final NodeResult execute = node.execute(flowContext);
            if (null == execute) {
                return NodeResult.fail(flowContext, "node result is null");
            }
            if (NodeState.FAILED == execute.getState()) {
                return NodeResult.fail(flowContext, "node state is failed");
            }
        }
        return NodeResult.builder().state(NodeState.COMPLETED).nodeContext(flowContext).build();
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.flow;
    }
}
