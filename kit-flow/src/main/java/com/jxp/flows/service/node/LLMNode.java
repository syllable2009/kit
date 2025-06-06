package com.jxp.flows.service.node;

import java.util.List;
import java.util.Map;

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
 * @author jiaxiaopeng
 * Created on 2025-06-05 10:23
 */
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class LLMNode extends AbstractNode {

    private String model;
    private String promot; // list-object
    private Map<String, String> config;
    private List<Param> output;

    @Override
    public boolean execute(FlowContext context) {
        // 找到模型调用
        log.info("找到模型调用");
        // 节点配置的参数取值逻辑
        final List<Param> input = this.getInput();
        FlowUtils.paramConvertMap(input, context);
        // 保存执行结果
        this.setNodeResult(NodeResult.success());
        context.putExecuteNode(this.getNodeId(), this);
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.largeModel;
    }

    @Override
    public String getNodeId() {
        return "LLMNode";
    }
}
