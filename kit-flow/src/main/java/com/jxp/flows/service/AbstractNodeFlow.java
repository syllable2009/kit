package com.jxp.flows.service;

import java.util.List;

import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeTypeEnum;

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
public abstract class AbstractNodeFlow implements IFlow {
    private String name;
    private String runId;
    private NodeTypeEnum type;
    // node或者workflow
    private List<INode> nodes;

    private List<Param> input;

    private List<Param> output;

    public INode add(INode node) {
        this.nodes.add(node);
        return this;
    }

    public INode add(List<INode> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }
}
