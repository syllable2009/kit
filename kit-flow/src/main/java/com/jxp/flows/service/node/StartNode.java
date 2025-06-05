package com.jxp.flows.service.node;

import org.apache.commons.lang3.BooleanUtils;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.service.AbstractNode;
import com.jxp.flows.service.FlowUtils;

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
    public boolean execute(FlowContext context) {
        if (null == context) {
            this.setNodeResult(NodeResult.fail("context is null"));
            context.putExecuteNode(this.getNodeId(), this);
            return false;
        }
        // 参数校验
        final boolean validResult = FlowUtils.validInputParam(this, context);
        if (BooleanUtils.isNotTrue(validResult)) {
            this.setNodeResult(NodeResult.fail("validInputParam failed"));
            context.putExecuteNode(this.getNodeId(), this);
            return false;
        }
        // 执行，构造返回,特别的开始节点的返回为：全局用户入参
        this.setNodeResult(NodeResult.success(context.getInput()));
        context.putExecuteNode(this.getNodeId(), this);
        return true;
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
