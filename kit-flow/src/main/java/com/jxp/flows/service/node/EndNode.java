package com.jxp.flows.service.node;

import com.jxp.flows.domain.FlowContext;
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
public class EndNode extends AbstractNode {

    @Override
    public boolean execute(FlowContext context) {
        // 获取配置的返回
//        final List<Param> output = this.getOutput();
//        if (CollectionUtils.isEmpty(output)) {
//            return NodeResult.success(context);
//        }
//        // 构造返回
//        this.setOutput(null);
//        context.putExecuteNode(this.getNodeId(), this);
//        return NodeResult.success(context);
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.end;
    }

    @Override
    public String getNodeId() {
        return "end";
    }
}
