package com.jxp.flows.service.flow;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;
import com.jxp.flows.infs.IPredicate;
import com.jxp.flows.service.AbstractNodeFlow;

import cn.hutool.core.lang.Pair;
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
public class ConditionWorkFlow extends AbstractNodeFlow {

    private List<Pair<IPredicate, INode>> conditions = Lists.newArrayList();
    private INode otherwise;

    @Override
    public String getNodeId() {
        return "ConditionWorkFlow";
    }

    @Override
    public boolean execute(FlowContext flowContext) {
        final List<Pair<IPredicate, INode>> nodes = this.getConditions();
        if (CollectionUtils.isEmpty(nodes)) {
            this.setNodeResult(NodeResult.fail("no node exec"));
            flowContext.putExecuteNode(this.getNodeId(), this);
            return false;
        }
        boolean conditionMatch = false;
        boolean execResult = false;
        // 判断条件,条件也是node
        for (Pair<IPredicate, INode> pair : nodes) {
            if (pair.getKey().apply(pair.getValue(), flowContext)) {
                conditionMatch = true;
                execResult = pair.getValue().execute(flowContext);
                if (execResult) {
                    this.setNodeResult(NodeResult.success());
                } else {
                    this.setNodeResult(NodeResult.fail("exec failed"));
                }
                break;
            }
        }

        // 没有进入条件
        if (BooleanUtils.isFalse(conditionMatch)) {
            if (null == otherwise) {
                this.setNodeResult(NodeResult.fail("otherwise is null"));
            } else {
                execResult = otherwise.execute(flowContext);
                if (execResult) {
                    this.setNodeResult(NodeResult.success());
                } else {
                    this.setNodeResult(NodeResult.fail("exec failed"));
                }
            }
        }

        flowContext.putExecuteNode(this.getNodeId(), this);
        return execResult;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.conditonFlow;
    }

    public static ConditionWorkFlow builder() {
        final ConditionWorkFlow workFlow = new ConditionWorkFlow();
        return workFlow;
    }

    public ConditionWorkFlow build() {
        return this;
    }


    public ConditionWorkFlow otherwise(INode node) {
        this.otherwise = node;
        return this;
    }


    public ConditionWorkFlow when(IPredicate predicate, INode node) {
        conditions.add(new Pair<>(predicate, node));
        return this;
    }


}
