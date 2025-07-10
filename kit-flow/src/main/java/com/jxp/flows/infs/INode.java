package com.jxp.flows.infs;

import java.util.Collections;
import java.util.List;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;

import cn.hutool.core.util.IdUtil;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:23
 */
public interface INode {

    default String getRunId() {
        return IdUtil.fastSimpleUUID();
    }

    // 对应db的uid
    String getNodeId();

    NodeTypeEnum getNodeType();

    String getName();

    void setNodeResult(NodeResult nodeResult);

    boolean execute(FlowContext context);

    default List<Param> getInput() {
        return Collections.EMPTY_LIST;
    }

    default NodeResult getResult() {
        return null;
    }
}
