package com.jxp.flows.service.flow;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.enums.NodeTypeEnum;
import com.jxp.flows.infs.INode;
import com.jxp.flows.service.AbstractNodeFlow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 并行
 * @author jiaxiaopeng
 * Created on 2025-06-04 14:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class ParallelWorkFlow extends AbstractNodeFlow {

    // 使用自定义线程池（避免占用公共ForkJoinPool）
    private static ExecutorService executor = Executors.newFixedThreadPool(
            Math.min(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2)
    );

    // node或者workflow
    private List<INode> nodes;

    public INode add(INode node) {
        this.nodes.add(node);
        return this;
    }

    public INode add(List<INode> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

    @Override
    public String getNodeId() {
        return "ParallelWorkFlow";
    }

    @Override
    public boolean execute(FlowContext flowContext) {
//        final List<INode> nodes = this.getNodes();
//        if (CollectionUtils.isEmpty(nodes)) {
//            return NodeResult.fail(flowContext, "no node exec");
//        }
//        // 并行执行
//        final List<NodeResult> result = nodes.stream()
//                .map(e -> CompletableFuture.supplyAsync(
//                        () -> e.execute(flowContext), executor))
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//        // 判断执行结果
//        for (NodeResult execute : result) {
//            if (null == execute) {
//                return NodeResult.fail(flowContext, "node result is null");
//            }
//            if (NodeState.FAILED == execute.getState()) {
//                return NodeResult.fail(flowContext, "node state is failed");
//            }
//        }
//        return NodeResult.builder().state(NodeState.COMPLETED).nodeContext(flowContext).build();
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.flow;
    }
}
