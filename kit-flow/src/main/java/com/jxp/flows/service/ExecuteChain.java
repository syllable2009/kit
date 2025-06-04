package com.jxp.flows.service;

import java.util.List;

import com.google.common.collect.Lists;
import com.jxp.flows.service.flow.SequentialWorkFlow;

/**
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:47
 */
public class ExecuteChain {

//    ExecuteChain execute(INode node);
//
//    ExecuteChain execute(List<INode> nodes);
//
//    ExecuteChain then(INode node);
//
//    ExecuteChain then(List<INode> nodes);
//
//    SequentialWorkFlow build();

    private List<INode> works;

    private ExecuteChain() {
    }

    public ExecuteChain then(INode node) {
        this.works.add(node);
        return this;
    }

    public ExecuteChain then(List<INode> nodes) {
        this.works.addAll(nodes);
        return this;
    }

    public static ExecuteChain builder() {
        final ExecuteChain executeChain = new ExecuteChain();
        executeChain.works = Lists.newArrayList();
        return executeChain;
    }

    public SequentialWorkFlow build() {
        final SequentialWorkFlow sequentialWorkFlow = new SequentialWorkFlow();
        sequentialWorkFlow.setNodes(this.works);
        return sequentialWorkFlow;
    }
}
