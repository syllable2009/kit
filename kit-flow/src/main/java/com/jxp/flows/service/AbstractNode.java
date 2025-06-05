package com.jxp.flows.service;

import java.util.List;

import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:30
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractNode implements INode {
    private String name;
    private String nodeId;
    private NodeTypeEnum type;
    // node
    private INode node;

    private List<Param> input;

    private NodeResult nodeResult;

    public INode set(INode node) {
        this.node = node;
        return this;
    }

    public abstract String getNodeId();
}
