package com.jxp.flows;

import com.google.common.collect.Lists;
import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.Param;
import com.jxp.flows.enums.ParamCategory;
import com.jxp.flows.enums.ParamType;
import com.jxp.flows.service.flow.SequentialWorkFlow;
import com.jxp.flows.service.node.EndNode;
import com.jxp.flows.service.node.LLMNode;
import com.jxp.flows.service.node.StartNode;

import lombok.extern.slf4j.Slf4j;

/**
 * 工作流的拆解与复用
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
                .category(ParamCategory.userInput)
                .type(ParamType.str)
                .required(true)
                .build())).build();
        final EndNode end = EndNode.builder().build();

        final LLMNode llmNode = LLMNode.builder().build();

        org.springframework.mock.web.MockMultipartFile file;
//        final NodeResult execute = workflow.execute(FlowContext.builder().build());
//        final NodeState state = execute.getState();
//        final FlowContext workContext = execute.getNodeContext();

        // 构建流程上下文对象
        final FlowContext context = FlowContext.builder()
                .input(Param.builder().name("userInput").value("给做作一首诗").build())
                .build();

        // 顺序执行块
        // 现需要一个chain把他们串起来
        final boolean execute = SequentialWorkFlow.builder()
                .then(start)
                .then(llmNode)
                .then(end)
                .build()
                .execute(context);

//        final boolean execute = ParallelWorkFlow.builder()
//                .add(Lists.newArrayList(start, llmNode, end))
//                .build()
//                .execute(context);

        log.info("execute:{},context:{}", execute, context);

//        final SequentialWorkFlow workflow = ExecuteChain.builder().then(start).then(end).then(end)
//                .build();

//
//        ConditionWorkFlow.builder().when((r, v) -> {
//                    System.out.println("1");
//                    return false;
//                }, null).when((r, v) -> {
//                    System.out.println("2");
//                    return false;
//                }, null).otherwise(null).build()
//                .execute(context);
    }
}
