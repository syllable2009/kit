package com.jxp.flows.service.node;

import java.util.List;
import java.util.UUID;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.service.AbstractNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 15:11
 */
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class StartNode extends AbstractNode {

    @Override
    public NodeResult execute(FlowContext context) {
        if (null == context) {
            return NodeResult.fail(context, "context is null");
        }
        context.setRunId(UUID.randomUUID().toString());
        // 参数校验
        final List<Param> input = this.getInput();
        final List<Param> input1 = context.getInput();
        // 执行，构造返回,特别的开始节点的返回为：全局用户入参
        this.setOutput(input);
        context.putExecuteNode(this.getNodeId(), this);
        return NodeResult.success(context);
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.start;
    }

    @Override
    public String getNodeId() {
        return "start";
    }
}
