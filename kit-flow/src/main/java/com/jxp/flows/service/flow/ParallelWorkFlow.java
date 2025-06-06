package com.jxp.flows.service.flow;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.jxp.flows.domain.FlowContext;
import com.jxp.flows.domain.NodeResult;
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


    public static ParallelWorkFlow builder() {
        final ParallelWorkFlow workFlow = new ParallelWorkFlow();
        workFlow.setNodes(Lists.newArrayList());
        return workFlow;
    }

    public ParallelWorkFlow build() {
        if (null == this.getInput()) {
            this.setInput(Collections.EMPTY_LIST);
        }
        return this;
    }

    public ParallelWorkFlow add(INode node) {
        this.nodes.add(node);
        return this;
    }

    public ParallelWorkFlow add(List<INode> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

    @Override
    public String getNodeId() {
        return "ParallelWorkFlow";
    }

    @Override
    public boolean execute(FlowContext flowContext) {
        final List<INode> nodes = this.getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            this.setNodeResult(NodeResult.fail("no node exec"));
            flowContext.getExecuteMap().put(this.getNodeId(), this);
            return false;
        }
        // 并行执行
        final List<Boolean> results = nodes.stream()
                .map(e -> CompletableFuture.supplyAsync(
                        () -> e.execute(flowContext), executor))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        // 判断执行结果
        this.setNodeResult(NodeResult.success());
        flowContext.putExecuteNode(this.getNodeId(), this);
        return true;
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.parallelFlow;
    }
}
