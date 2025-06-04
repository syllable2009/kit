package com.jxp.flows.infs;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:23
 */
public interface INode {

    String getNodeId();

    String getName();

    void setNodeResult(NodeResult nodeResult);

    default String getRunId() {
        return UUID.randomUUID().toString();
    }

    boolean execute(FlowContext context);

    NodeTypeEnum getNodeType();

    default List<Param> getInput() {
        return Collections.EMPTY_LIST;
    }

    default NodeResult getResult() {
        return null;
    }
}
