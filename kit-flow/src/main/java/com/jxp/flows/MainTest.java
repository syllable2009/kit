package com.jxp.flows;

import com.google.common.collect.Lists;
import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.NodeState;
import com.jxp.flows.service.ExecuteChain;
import com.jxp.flows.service.flow.SequentialWorkFlow;
import com.jxp.flows.service.node.EndNode;
import com.jxp.flows.service.node.StartNode;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-28 10:56
 */

@Slf4j
public class MainTest {

    public static void main(String[] args) {
//        SequentialWorkFlow flow1 = new SequentialWorkFlow();
//        flow1.add();
//        flow1.add();
//        flow1.execute();
        final StartNode start = StartNode.builder().input(Lists.newArrayList(Param.builder()
                .name("userInput")
                .build())).build();
        final EndNode end = EndNode.builder().build();

        // 现需要一个chain把他们串起来
        final SequentialWorkFlow workflow = ExecuteChain.builder().then(start).then(end).then(end)
                .build();


        final NodeResult execute = workflow.execute(FlowContext.builder().build());
        final NodeState state = execute.getState();
        final FlowContext workContext = execute.getNodeContext();
    }
}
